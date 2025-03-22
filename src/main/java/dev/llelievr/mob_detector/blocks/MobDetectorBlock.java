package dev.llelievr.mob_detector.blocks;

import dev.llelievr.mob_detector.blockentities.MobDetectorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MobDetectorBlock extends BaseBlock implements EntityBlock {
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MobDetectorEntity(blockPos, blockState);
    }
}
