package dev.yurisuika.compost.mixin.mods;

import dev.yurisuika.compost.world.level.block.entity.ContainerComposterBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.VanillaInventoryCodeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class NeoForgeMixin {

    @Mixin(value = VanillaInventoryCodeHooks.class, remap = false)
    public abstract static class VanillaInventoryCodeHooksMixin {

        @Shadow
        private static ItemStack insertStack(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot) {
            return null;
        }

        @Inject(method = "putStackInInventoryAllSlots", at = @At("HEAD"), cancellable = true)
        private static void useComposterInputSlot(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
            if (destination instanceof ContainerComposterBlockEntity) {
                cir.setReturnValue(insertStack(source, destination, destInventory, stack, 27));
            }
        }

    }

}