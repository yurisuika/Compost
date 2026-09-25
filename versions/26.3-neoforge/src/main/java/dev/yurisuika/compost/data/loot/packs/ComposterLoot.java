package dev.yurisuika.compost.data.loot.packs;

import dev.yurisuika.compost.world.level.storage.loot.CompostLootTables;
import dev.yurisuika.compost.world.level.storage.loot.predicates.MatchCompostable;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public record ComposterLoot(LootTableSubProvider.Context context) implements LootTableSubProvider {

    public static LootTable.Builder create(HolderGetter<Item> lookup) {
        return LootTable.lootTable()
                .setRandomSequence(CompostLootTables.COMPOSTERS_COMPOST.identifier())
                .withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(Items.DIRT)
                                .setWeight(2))
                        .add(LootItem.lootTableItem(Items.COARSE_DIRT)
                                .when(AnyOfCondition.anyOf(
                                        MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                                .of(lookup, ItemTags.SAPLINGS)),
                                        MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                                .of(lookup, ItemTags.LEAVES))))))
                .withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(1))
                        .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                .of(lookup, ItemTags.VILLAGER_PLANTABLE_SEEDS)))
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
                                                .of(lookup, Items.BROWN_MUSHROOM)))))
                        .add(LootItem.lootTableItem(Items.RED_MUSHROOM)
                                .when(AnyOfCondition.anyOf(
                                        AllOfCondition.allOf(
                                                LocationCheck.checkLocation(LocationPredicate.Builder.location()
                                                        .setDimension(Level.OVERWORLD)
                                                        .setCanSeeSky(true)),
                                                WeatherCheck.weather()
                                                        .setRaining(true)),
                                        MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                                .of(lookup, Items.RED_MUSHROOM)))))
                        .add(LootItem.lootTableItem(Items.WARPED_FUNGUS)
                                .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                        .of(lookup, Items.WARPED_FUNGUS))))
                        .add(LootItem.lootTableItem(Items.CRIMSON_FUNGUS)
                                .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                        .of(lookup, Items.CRIMSON_FUNGUS)))));
    }

    @Override
    public void run() {
        context.accept(CompostLootTables.COMPOSTERS_COMPOST, create(context.lookup(Registries.ITEM)));
    }

}