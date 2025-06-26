package dev.yurisuika.compost.mixin.mods;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

public abstract class FabricMixin {

    @Pseudo
    @SuppressWarnings("UnstableApiUsage")
    @Mixin(ItemStorage.class)
    public abstract static class ItemStorageMixin {

//        @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/lookup/v1/block/BlockApiLookup;registerForBlocks(Lnet/fabricmc/fabric/api/lookup/v1/block/BlockApiLookup$BlockApiProvider;[Lnet/minecraft/world/level/block/Block;)V", ordinal = 0))
//        private static void redirectRegisterForBlocks(BlockApiLookup instance, BlockApiLookup.BlockApiProvider<?, ?> acBlockApiProvider, Block[] blocks) {}

    }

}