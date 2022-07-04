package org.teacon.nocaet.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TransparentLogBlock extends RotatedPillarBlock {

    public TransparentLogBlock(Properties properties) {
        super(properties.noOcclusion().isSuffocating((a, b, c) -> false).isViewBlocking((a, b, c) -> false));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return true;
    }
}
