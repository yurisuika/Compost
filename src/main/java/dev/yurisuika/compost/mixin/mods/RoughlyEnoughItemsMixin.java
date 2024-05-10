package dev.yurisuika.compost.mixin.mods;

import dev.yurisuika.compost.util.NetworkUtil;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.DefaultClientPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.List;

public abstract class RoughlyEnoughItemsMixin {

    @Pseudo
    @SuppressWarnings("UnstableApiUsage")
    @Mixin(DefaultClientPlugin.class)
    public abstract static class DefaultClientPluginMixin {

        @Redirect(method = "registerDisplays", at = @At(value = "INVOKE", target = "Ljava/util/Collections;singletonList(Ljava/lang/Object;)Ljava/util/List;"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/ComposterBlock;registerDefaultCompostableItems()V"), to = @At(value = "INVOKE", target = "Lme/shedaniel/rei/plugin/client/DefaultClientPlugin$DummyAxeItem;getStrippedBlocksMap()Ljava/util/Map;")))
        private <T> List<EntryIngredient> redirectCompostingOutput(T o) {
            EntryIngredient.Builder output = EntryIngredient.builder();
            NetworkUtil.stacks.forEach(stack -> output.add(EntryStacks.of(stack)));
            return List.of(output.build());
        }

    }

}