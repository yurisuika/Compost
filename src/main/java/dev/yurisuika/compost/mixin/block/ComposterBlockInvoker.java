package dev.yurisuika.compost.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ComposterBlock.class)
public interface ComposterBlockInvoker {

    @Invoker("addToComposter")
    static BlockState invokeAddToComposter(Entity user, BlockState state, WorldAccess world, BlockPos pos, ItemStack itemStack) {
        throw new AssertionError();
    }

}