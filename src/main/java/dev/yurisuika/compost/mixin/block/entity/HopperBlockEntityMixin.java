package dev.yurisuika.compost.mixin.block.entity;

import net.minecraft.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

    @Redirect(method = "ejectItems", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/VanillaInventoryCodeHooks;insertHook(Lnet/minecraft/block/entity/HopperBlockEntity;)Z"))
    private static boolean redirectEjectItems(HopperBlockEntity hopper) {
        return false;
    }

}