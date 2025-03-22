package dev.llelievr.mob_detector.peripheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import dev.llelievr.mob_detector.blockentities.MobDetectorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

public class PeripheralHandler implements IPeripheralProvider {
    @Override
    public LazyOptional<IPeripheral> getPeripheral(Level level, BlockPos blockPos, Direction direction) {
        BlockEntity tile = level.getBlockEntity(blockPos);

        if (tile instanceof MobDetectorEntity mobDetectorEntity) return LazyOptional.of(() -> new MobDetectorPeripheral(mobDetectorEntity));
        return LazyOptional.empty();
    }
}
