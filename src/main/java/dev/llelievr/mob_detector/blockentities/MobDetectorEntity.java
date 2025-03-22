package dev.llelievr.mob_detector.blockentities;

import dev.llelievr.mob_detector.MobDetector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MobDetectorEntity extends BlockEntity {

    public MobDetectorEntity(BlockPos p_155622_, BlockState p_155623_) {
        super(MobDetector.MOB_DETECTOR_TILE.get(), p_155622_, p_155623_);
    }
}
