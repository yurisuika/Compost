package dev.yurisuika.compost.data.loot.packs;

import dev.yurisuika.compost.world.level.storage.loot.CompostLootTables;
import dev.yurisuika.compost.world.level.storage.loot.predicates.MatchCompostable;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public record ComposterLoot(HolderLookup.Provider registries) implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(CompostLootTables.COMPOSTER, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.DIRT)
                                .setWeight(2))
                        .add(LootItem.lootTableItem(Items.COARSE_DIRT)
                                .when(AnyOfCondition.anyOf(
                                        MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                                .of(registries.lookupOrThrow(Registries.ITEM), ItemTags.SAPLINGS)),
                                        MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                                .of(registries.lookupOrThrow(Registries.ITEM), ItemTags.LEAVES))))))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                .of(registries.lookupOrThrow(Registries.ITEM), ItemTags.VILLAGER_PLANTABLE_SEEDS)))
                        .add(LootItem.lootTableItem(Items.BONE_MEAL)))
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(1.0F, 2.0F))
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
                                                .of(registries.lookupOrThrow(Registries.ITEM), Items.BROWN_MUSHROOM)))))
                        .add(LootItem.lootTableItem(Items.RED_MUSHROOM)
                                .when(AnyOfCondition.anyOf(
                                        AllOfCondition.allOf(
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location()
                                                        .setDimension(Level.OVERWORLD)
                                                        .setCanSeeSky(true)),
                                                WeatherCheck.weather()
                                                        .setRaining(true)),
                                        MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                                .of(registries.lookupOrThrow(Registries.ITEM), Items.RED_MUSHROOM)))))
                        .add(LootItem.lootTableItem(Items.WARPED_FUNGUS)
                                .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                        .of(registries.lookupOrThrow(Registries.ITEM), Items.WARPED_FUNGUS))))
                        .add(LootItem.lootTableItem(Items.CRIMSON_FUNGUS)
                                .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                        .of(registries.lookupOrThrow(Registries.ITEM), Items.CRIMSON_FUNGUS))))));
    }

}