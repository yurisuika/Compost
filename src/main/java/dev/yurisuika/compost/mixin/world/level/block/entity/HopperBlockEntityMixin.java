package dev.yurisuika.compost.mixin.world.level.block.entity;

import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = HopperBlockEntity.class, remap = false)
public abstract class HopperBlockEntityMixin {

    @Redirect(method = "ejectItems", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/items/VanillaInventoryCodeHooks;insertHook(Lnet/minecraft/world/level/block/entity/HopperBlockEntity;)Z"))
    private static boolean removeHook(HopperBlockEntity hopper) {
        return false;
    }

}