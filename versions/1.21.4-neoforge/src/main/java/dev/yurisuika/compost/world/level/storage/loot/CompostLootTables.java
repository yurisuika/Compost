package dev.yurisuika.compost.world.level.storage.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public class CompostLootTables {

    public static final ResourceKey<LootTable> COMPOSTER = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("compost", "composter"));

}