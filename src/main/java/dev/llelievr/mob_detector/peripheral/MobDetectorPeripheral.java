package dev.llelievr.mob_detector.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.ObjectLuaTable;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.llelievr.mob_detector.blockentities.MobDetectorEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

record Position(double x, double y, double z) {}

record LocatedEntity(
        String type,
        String name,
        double distance,
        int id
) {}

record LocatedEntityDetail(
        String type,
        String name,
        Object position,
        Object eyePosition,
        double distance,
        boolean directSight,
        int id
) {}

public class MobDetectorPeripheral implements IPeripheral {
    private final MobDetectorEntity detectorTile;

    private static final EntityTypeTest<Entity, ?> ANY_TYPE = new EntityTypeTest<Entity, Entity>() {
        public Entity tryCast(Entity p_175109_) {
            return p_175109_;
        }

        public Class<? extends Entity> getBaseClass() {
            return Entity.class;
        }
    };

    public MobDetectorPeripheral(MobDetectorEntity entity) {
        this.detectorTile = entity;
    }

    private Map<String, Object> objectToMap(Object object) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field: fields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }


    @Override
    public String getType() {
        return "mob_detector";
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    @LuaFunction()
    public final ObjectLuaTable getSurroundingMobsWithType(String type) {
        if (this.detectorTile.getLevel() == null)
            return null;
        EntityTypeTest<Entity, Entity> test = new EntityTypeTest<>() {
            @Override
            public @Nullable Entity tryCast(Entity entity) {
                if (entity.getType().builtInRegistryHolder().key().location().toString().equals(type))
                    return entity;
                return null;
            }

            @Override
            public Class<? extends Entity> getBaseClass() {
                return Entity.class;
            }
        };
        Map<Integer, Object> out = new HashMap<>();
        int i = 0;
        for (Entity entity : this.detectorTile.getLevel().getEntities(test, new AABB(this.detectorTile.getBlockPos()).inflate(64), Entity::isAlive)) {
            Vec3 midPos = entity.getEyePosition(0).add(entity.getPosition(0.0f)).multiply(0.5, 0.5, 0.5);
            out.put(i++, objectToMap(new LocatedEntity(
                entity.getType().builtInRegistryHolder().key().location().toString(),
                entity.getName().getString(),
                this.detectorTile.getBlockPos().getCenter().distanceTo(midPos),
                entity.getId()
            )));
        }
        return new ObjectLuaTable(out);
    }

    @LuaFunction()
    public final ObjectLuaTable getSurroundingMobs() {
        if (this.detectorTile.getLevel() == null)
            return null;
        Map<Integer, Object> out = new HashMap<>();
        int i = 0;
        for (Entity entity : this.detectorTile.getLevel().getEntities(ANY_TYPE, new AABB(this.detectorTile.getBlockPos()).inflate(64), Entity::isAlive)) {
            Vec3 midPos = entity.getEyePosition(0).add(entity.getPosition(0.0f)).multiply(0.5, 0.5, 0.5);
            out.put(i++, objectToMap(new LocatedEntity(
                    entity.getType().builtInRegistryHolder().key().location().toString(),
                    entity.getName().getString(),
                    this.detectorTile.getBlockPos().getCenter().distanceTo(midPos),
                    entity.getId()
            )));
        }
        return new ObjectLuaTable(out);
    }

    public boolean isObstructed(Level level, Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start).normalize().scale(0.6);
        Vec3 adjustedStart = start.add(direction);

        // Define the type of raycast (blocks only, ignoring fluids)
        ClipContext context = new ClipContext(
                adjustedStart,
                end,
                ClipContext.Block.COLLIDER, // Stops at solid blocks
                ClipContext.Fluid.NONE,     // Ignores fluids
                null                        // No specific entity to ignore
        );

        // Perform the raycast
        HitResult result = level.clip(context);

        // Check if the ray hit something before reaching the destination
        return result.getType() == HitResult.Type.BLOCK;
    }

    @LuaFunction
    public final Object getMob(int id) {
        if (this.detectorTile.getLevel() == null)
            return null;
        Entity e = this.detectorTile.getLevel().getEntity(id);
        if (e == null)
            return null;
        Vec3 midPos = e.getEyePosition(0).add(e.getPosition(0.0f)).multiply(0.5, 0.5, 0.5);
        double distance = Math.sqrt(this.detectorTile.getBlockPos().distToLowCornerSqr(midPos.x, midPos.y, midPos.z));
        if (distance > 64d) {
            return null;
        }

        boolean directSight = !isObstructed(e.level(), this.detectorTile.getBlockPos().getCenter(), midPos);

        Vec3 pos = e.getPosition(0);
        Vec3 eyePos = e.getEyePosition(0);

        return objectToMap(
            new LocatedEntityDetail(
                e.getType().builtInRegistryHolder().key().location().toString(),
                e.getName().getString(),
                objectToMap(new Position(pos.x, pos.y, pos.z)),
                objectToMap(new Position(eyePos.x, eyePos.y, eyePos.z)),
                distance,
                directSight,
                e.getId()
            )
        );
    }

    @LuaFunction
    public final Object getMobFromOffset(int id, double offsetX, double offsetY, double offsetZ) {
        if (this.detectorTile.getLevel() == null)
            return null;
        Entity e = this.detectorTile.getLevel().getEntity(id);
        if (e == null)
            return null;
        Vec3 midPos = e.getEyePosition(0).add(e.getPosition(0.0f)).multiply(0.5, 0.5, 0.5);
        Vec3 offset = new Vec3(offsetX, offsetY, offsetZ);
        Vec3 posStart = this.detectorTile.getBlockPos().getCenter().add(offset);
        double distToBlock = this.detectorTile.getBlockPos().getCenter().distanceToSqr(posStart);
        if (distToBlock > 10*10) {
            return null;
        }
        double distance = posStart.distanceTo(midPos);
        if (distance > 64d) {
            return null;
        }
        boolean directSight = !isObstructed(e.level(), posStart, midPos);

        Vec3 pos = e.getPosition(0);
        Vec3 eyePos = e.getEyePosition(0);

        return objectToMap(
                new LocatedEntityDetail(
                        e.getType().builtInRegistryHolder().key().location().toString(),
                        e.getName().getString(),
                        objectToMap(new Position(pos.x, pos.y, pos.z)),
                        objectToMap(new Position(eyePos.x, eyePos.y, eyePos.z)),
                        distance,
                        directSight,
                        e.getId()
                )
        );
    }
}
