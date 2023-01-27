package dev.yurisuika.compost.mixin.mods;

import me.shedaniel.rei.plugin.client.DefaultClientPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Iterator;

public class RoughlyEnoughItemsMixin {

    @Mixin(DefaultClientPlugin.class)
    public static class DefaultClientPluginMixin {

        @Redirect(method = "registerDisplays", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/ComposterBlock;registerDefaultCompostableItems()V"), to = @At(value = "INVOKE", target = "Lme/shedaniel/rei/plugin/client/DefaultClientPlugin$DummyAxeItem;getStrippedBlocksMap()Ljava/util/Map;")))
        private boolean redirectRegisterDisplays(Iterator instance) {
            return false;
        }

    }

}