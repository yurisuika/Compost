package dev.yurisuika.compost.world.level.storage.loot.parameters;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CompostLootContextParams {

    public static final ContextKey<List<ItemStack>> COMPOSTABLES = new ContextKey<>(ResourceLocation.fromNamespaceAndPath("compost", "compostables"));

}