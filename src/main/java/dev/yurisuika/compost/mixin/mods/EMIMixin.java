package dev.yurisuika.compost.mixin.mods;

import dev.emi.emi.VanillaPlugin;
import dev.emi.emi.api.EmiRegistry;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

public abstract class EMIMixin {

    @Pseudo
    @Mixin(value = VanillaPlugin.class, remap = false)
    public abstract static class VanillaPluginMixin {

        @Inject(method = "addComposting", at = @At("HEAD"), cancellable = true)
        private static void injectCancel(EmiRegistry registry, Set<Item> hiddenItems, CallbackInfo ci) {
            ci.cancel();
        }

    }

}