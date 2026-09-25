package dev.yurisuika.compost.data.loot;

import dev.yurisuika.compost.data.loot.packs.ComposterLoot;
import dev.yurisuika.compost.world.level.storage.loot.CompostLootTables;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import dev.yurisuika.compost.world.level.storage.loot.predicates.MatchCompostable;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

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
            output.accept(CompostLootTables.COMPOSTERS_COMPOST, LootTable.lootTable()
                    .setRandomSequence(CompostLootTables.COMPOSTERS_COMPOST.identifier())
                    .withPool(LootPool.lootPool()
                            .setRolls(ContextIntProviders.exactly(1))
                            .add(LootItem.lootTableItem(Items.DIRT)
                                    .setWeight(2))
                            .add(LootItem.lootTableItem(Items.COARSE_DIRT)
                                    .when(AnyOfCondition.anyOf(
                                            MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                                    .of(registries.join().lookupOrThrow(Registries.ITEM), ItemTags.SAPLINGS)),
                                            MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                                    .of(registries.join().lookupOrThrow(Registries.ITEM), ItemTags.LEAVES))))))
                    .withPool(LootPool.lootPool()
                            .setRolls(ContextIntProviders.exactly(1))
                            .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                    .of(registries.join().lookupOrThrow(Registries.ITEM), ItemTags.VILLAGER_PLANTABLE_SEEDS)))
                            .add(LootItem.lootTableItem(Items.BONE_MEAL)))
                    .withPool(LootPool.lootPool()
                            .setRolls(ContextIntProviders.between(1, 2))
                            .when(LootItemRandomChanceCondition.randomChance(0.25F))
                            .add(LootItem.lootTableItem(Items.BROWN_MUSHROOM)
                                    .when(AnyOfCondition.anyOf(
                                            AllOfCondition.allOf(
                                                    LocationCheck.checkLocation(LocationPredicate.Builder.location()
                                                            .setDimension(Level.OVERWORLD)
                                                            .setCanSeeSky(true)),
                                                    WeatherCheck.weather()
                                                            .setRaining(true)),
                                            MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                                    .of(registries.join().lookupOrThrow(Registries.ITEM), Items.BROWN_MUSHROOM)))))
                            .add(LootItem.lootTableItem(Items.RED_MUSHROOM)
                                    .when(AnyOfCondition.anyOf(
                                            AllOfCondition.allOf(
                                                    LocationCheck.checkLocation(LocationPredicate.Builder.location()
                                                            .setDimension(Level.OVERWORLD)
                                                            .setCanSeeSky(true)),
                                                    WeatherCheck.weather()
                                                            .setRaining(true)),
                                            MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                                    .of(registries.join().lookupOrThrow(Registries.ITEM), Items.RED_MUSHROOM)))))
                            .add(LootItem.lootTableItem(Items.WARPED_FUNGUS)
                                    .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                            .of(registries.join().lookupOrThrow(Registries.ITEM), Items.WARPED_FUNGUS))))
                            .add(LootItem.lootTableItem(Items.CRIMSON_FUNGUS)
                                    .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                            .of(registries.join().lookupOrThrow(Registries.ITEM), Items.CRIMSON_FUNGUS))))));
        }

        @Override
        public void run() {}

    }

}