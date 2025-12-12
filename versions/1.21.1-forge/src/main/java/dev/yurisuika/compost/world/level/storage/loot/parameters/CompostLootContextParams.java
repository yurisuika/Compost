package dev.yurisuika.compost.world.level.storage.loot.parameters;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

import java.util.List;

public class CompostLootContextParams {

    public static final LootContextParam<List<ItemStack>> COMPOSTABLES = new LootContextParam<>(ResourceLocation.fromNamespaceAndPath("compost", "compostables"));

}