package dev.yurisuika.compost.mixin.mods;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiCompostingRecipe;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static dev.yurisuika.compost.client.option.CompostConfig.*;

public abstract class EMIMixin {

    @Pseudo
    @Mixin(value = EmiCompostingRecipe.class, remap = false)
    public abstract static class EmiCompostingRecipeMixin {

        @Inject(method = "addWidgets", at = @At(value = "INVOKE", target = "Ldev/emi/emi/api/widget/WidgetHolder;addSlot(Ldev/emi/emi/api/stack/EmiIngredient;II)Ldev/emi/emi/api/widget/SlotWidget;", ordinal = 1), cancellable = true)
        private void injectWidgetAndCancel(WidgetHolder widgets, CallbackInfo ci) {
            widgets.addGeneratedSlot(this::getStacks, EmiUtil.RANDOM.nextInt(), 90, 0).recipeContext((EmiCompostingRecipe)(Object)this);
            ci.cancel();

        }

        @Unique
        private EmiStack getStacks(Random random) {
            Config.Group group = config.items[random.nextInt(config.items.length)];
            ItemStack stack = createItemStack(group);
            stack.setCount((group.min + group.max) / 2);
            return EmiStack.of(stack).setAmount((group.min + group.max) / 2).setChance((float)group.chance);
        }

    }

}