package dev.yurisuika.compost.world.level.storage.loot;

import dev.yurisuika.compost.Compost;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public class CompostLootTables {

    public static final ResourceKey<LootTable> COMPOSTERS_COMPOST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Compost.MOD_ID, "composters/compost"));

}