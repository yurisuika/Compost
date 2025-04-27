package dev.yurisuika.compost.mixin.mods;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class RoughlyEnoughItemsMixin {

    @Pseudo
    @Mixin(DefaultPlugin.class)
    public abstract static class DefaultPluginMixin {

        @ModifyArg(method = "registerRecipeDisplays", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;<init>(Lnet/minecraft/world/level/ItemLike;)V"), index = 0, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/ComposterBlock;bootStrap()V"), to = @At(value = "INVOKE", target = "Lme/shedaniel/rei/plugin/stripping/DummyAxeItem;getStrippedBlocksMap()Ljava/util/Map;")))
        private ItemLike redirectCompostingOutput(ItemLike item) {
            List<ItemStack> output = new ArrayList<>();
            Network.getProduce().forEach(produce -> output.add(Parse.createItemStack(produce)));
            return output.get(ThreadLocalRandom.current().nextInt(output.size())).getItem();
        }

    }

}