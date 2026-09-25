package dev.yurisuika.compost.data.loot;

import dev.yurisuika.compost.data.loot.packs.ComposterLoot;
import dev.yurisuika.compost.world.level.storage.loot.CompostLootTables;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CompostLootTableProvider {

    public static LootTableProvider create() {
        return new LootTableProvider(Set.of(CompostLootTables.COMPOSTERS_COMPOST), List.of(new LootTableProvider.SubProviderEntry(ComposterLoot::new, CompostLootContextParamSets.COMPOSTER)));
    }

    public static class Fabric extends SimpleFabricLootTableSubProvider {

        private final CompletableFuture<HolderLookup.Provider> registries;

        public Fabric(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, CompostLootContextParamSets.COMPOSTER);
            this.registries = registries;
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
            output.accept(CompostLootTables.COMPOSTERS_COMPOST, ComposterLoot.create(registries.join().lookupOrThrow(Registries.ITEM)));
        }

        @Override
        public void run() {}

    }

}