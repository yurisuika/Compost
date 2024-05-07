package dev.yurisuika.compost.mixin.mods;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiCompostingRecipe;
import dev.yurisuika.compost.server.option.CompostConfig;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;

import static dev.yurisuika.compost.CompostClient.*;
import static dev.yurisuika.compost.util.ConfigUtil.*;

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
            CompostConfig.Config.World.Group group = List.of(GROUPS).get(random.nextInt(GROUPS.length));
            ItemStack stack = createItemStack(group);
            return EmiStack.of(stack).setAmount((group.min + group.max) / 2).setChance((float)group.chance);
        }

    }

}