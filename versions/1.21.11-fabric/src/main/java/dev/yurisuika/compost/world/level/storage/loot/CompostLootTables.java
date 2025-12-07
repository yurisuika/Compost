package dev.yurisuika.compost.world.level.storage.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class CompostLootTables {

    public static final ResourceKey<LootTable> COMPOSTERS_COMPOST = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("compost", "composters/compost"));

}