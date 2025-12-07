package dev.yurisuika.compost.data.loot.packs;

import dev.yurisuika.compost.world.level.storage.loot.CompostLootTables;
import dev.yurisuika.compost.world.level.storage.loot.predicates.MatchCompostable;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.AlternativeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.WeatherCheck;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class ComposterLoot implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
        output.accept(CompostLootTables.COMPOSTERS_COMPOST, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.DIRT)
                                .setWeight(2))
                        .add(LootItem.lootTableItem(Items.COARSE_DIRT)
                                .when(AlternativeLootItemCondition.alternative(
                                        MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                                .of(ItemTags.SAPLINGS)),
                                        MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                                .of(ItemTags.LEAVES))))))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                .of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS)))
                        .add(LootItem.lootTableItem(Items.BONE_MEAL)))
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(1.0F, 2.0F))
                        .when(LootItemRandomChanceCondition.randomChance(0.25F))
                        .add(LootItem.lootTableItem(Items.BROWN_MUSHROOM)
                                .when(LocationCheck.checkLocation(LocationPredicate.Builder.location()
                                                .setDimension(Level.OVERWORLD)))
                                .when(WeatherCheck.weather()
                                        .setRaining(true)))
                        .add(LootItem.lootTableItem(Items.BROWN_MUSHROOM)
                                .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                                .of(Items.BROWN_MUSHROOM))))
                        .add(LootItem.lootTableItem(Items.RED_MUSHROOM)
                                .when(LocationCheck.checkLocation(LocationPredicate.Builder.location()
                                        .setDimension(Level.OVERWORLD)))
                                .when(WeatherCheck.weather()
                                        .setRaining(true)))
                        .add(LootItem.lootTableItem(Items.RED_MUSHROOM)
                                .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                        .of(Items.RED_MUSHROOM))))
                        .add(LootItem.lootTableItem(Items.WARPED_FUNGUS)
                                .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                        .of(Items.WARPED_FUNGUS))))
                        .add(LootItem.lootTableItem(Items.CRIMSON_FUNGUS)
                                .when(MatchCompostable.compostableMatches(ItemPredicate.Builder.item()
                                        .of(Items.CRIMSON_FUNGUS))))));
    }

}