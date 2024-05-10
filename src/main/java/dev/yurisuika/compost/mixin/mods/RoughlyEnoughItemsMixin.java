package dev.yurisuika.compost.mixin.mods;

import dev.yurisuika.compost.util.NetworkUtil;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
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

        @ModifyArg(method = "registerRecipeDisplays", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/ItemConvertible;)V"), index = 0, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/ComposterBlock;registerDefaultCompostableItems()V"), to = @At(value = "INVOKE", target = "Lme/shedaniel/rei/plugin/stripping/DummyAxeItem;getStrippedBlocksMap()Ljava/util/Map;")))
        private ItemConvertible redirectCompostingOutput(ItemConvertible item) {
            List<ItemStack> output = new ArrayList<>();
            NetworkUtil.stacks.forEach(stack -> output.add(stack));
            return output.get(ThreadLocalRandom.current().nextInt(output.size())).getItem();
        }

    }

}