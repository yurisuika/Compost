package com.yurisuika.compost.mixin.blocks;

import net.minecraft.block.ComposterBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {

    @Redirect(method = "emptyFullComposter", at = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;BONE_MEAL:Lnet/minecraft/item/Item;", opcode = Opcodes.GETSTATIC))
    private static Item redirectEmptyFullComposter() {
        return Items.DIRT;
    }

    @Redirect(method = "getInventory", at = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;BONE_MEAL:Lnet/minecraft/item/Item;", opcode = Opcodes.GETSTATIC))
    private Item redirectGetInventory() {
        return Items.DIRT;
    }

}
