package com.yurisuika.compost.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ComposterBlock.class)
public interface ComposterBlockInvoker {

    @Invoker("emptyComposter")
    static BlockState invokeEmptyComposter(BlockState state, WorldAccess world, BlockPos pos) {
        throw new AssertionError();
    }

}
