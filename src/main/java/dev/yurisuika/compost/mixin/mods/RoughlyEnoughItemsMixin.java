package dev.yurisuika.compost.mixin.mods;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.DefaultClientPlugin;
import net.minecraft.client.Minecraft;
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

        @Redirect(method = "registerDisplays", at = @At(value = "INVOKE", target = "Ljava/util/Collections;singletonList(Ljava/lang/Object;)Ljava/util/List;"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/ComposterBlock;bootStrap()V"), to = @At(value = "INVOKE", target = "Lme/shedaniel/rei/plugin/client/DefaultClientPlugin$DummyAxeItem;getStrippedBlocksMap()Ljava/util/Map;")))
        private <T> List<EntryIngredient> redirectCompostingOutput(T o) {
            EntryIngredient.Builder output = EntryIngredient.builder();
            Parse.createNetworkCompost(Minecraft.getInstance().level.registryAccess(), Network.getLevelName()).forEach(compost -> output.add(EntryStacks.of(compost.copyWithCount(1))));
            return List.of(output.build());
        }

    }

}