package dev.yurisuika.compost;

import dev.yurisuika.compost.world.level.block.entity.CompostBlockEntityType;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import dev.yurisuika.compost.world.level.storage.loot.predicates.CompostLootItemConditions;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class Compost implements ModInitializer {

    public static void registerLootContextKeySets() {
        LootContextParamSets.REGISTRY.put(new ResourceLocation("compost", "composter"), CompostLootContextParamSets.COMPOSTER);
    }

    public static void registerLootItemConditionTypes() {
        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation("compost", "match_compostable"), CompostLootItemConditions.MATCH_COMPOSTABLE);
    }

    public static void registerBlockEntityTypes() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation("compost", "composter"), CompostBlockEntityType.COMPOSTER);
    }

    @Override
    public void onInitialize() {
        registerLootContextKeySets();
        registerLootItemConditionTypes();
        registerBlockEntityTypes();
    }

}