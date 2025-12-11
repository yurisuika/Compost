package dev.yurisuika.compost.world.level.storage.loot.parameters;

import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;

public class CompostLootContextParams {

    public static final ContextKey<ItemStack> COMPOSTABLE = new ContextKey<>(Identifier.fromNamespaceAndPath("compost", "compostable"));

}