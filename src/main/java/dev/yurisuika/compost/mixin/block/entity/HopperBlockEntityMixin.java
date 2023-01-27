package dev.yurisuika.compost.mixin.block.entity;

import dev.yurisuika.compost.block.ArrayComposterInventory;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.entity.HopperBlockEntity.transfer;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

    @Inject(method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true)
    private static void injectExtract(Hopper hopper, Inventory inventory, int slot, Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (inventory instanceof ArrayComposterInventory) {
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack composterItemStack = transfer(inventory, hopper, inventory.removeStack(i, inventory.getStack(i).getCount()), null);
            }
        }
    }

}