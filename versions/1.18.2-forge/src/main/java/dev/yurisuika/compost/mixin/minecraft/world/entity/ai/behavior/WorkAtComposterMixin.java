package dev.yurisuika.compost.mixin.minecraft.world.entity.ai.behavior;

import dev.yurisuika.compost.world.level.block.ContainerComposterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.WorkAtComposter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorkAtComposter.class)
public class WorkAtComposterMixin {

    @Redirect(method = "compostItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/ComposterBlock;insertItem(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState redirectComposter(BlockState state, ServerLevel level, ItemStack stack, BlockPos pos) {
        return ContainerComposterBlock.insertItem(state, level, stack, pos);
    }

    @Redirect(method = "compostItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/ComposterBlock;extractProduce(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState redirectComposter(BlockState state, Level level, BlockPos pos) {
        return ContainerComposterBlock.extractProduce(state, level, pos);
    }

}