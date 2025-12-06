package dev.yurisuika.compost.world.level.storage.loot.parameters;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class CompostLootContextParams {

    public static final LootContextParam<ItemStack> COMPOSTABLE = new LootContextParam<>(ResourceLocation.fromNamespaceAndPath("compost", "compostable"));

}