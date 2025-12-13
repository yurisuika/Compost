package dev.yurisuika.compost;

import dev.yurisuika.compost.world.level.block.entity.CompostBlockEntityType;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import dev.yurisuika.compost.world.level.storage.loot.predicates.CompostLootItemConditions;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Compost implements ModInitializer {

    public static final String MOD_ID = "compost";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void registerLootContextKeySets() {
        LootContextParamSets.REGISTRY.put(Identifier.fromNamespaceAndPath(MOD_ID, "composter"), CompostLootContextParamSets.COMPOSTER);
    }

    public static void registerLootItemConditionTypes() {
        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "match_compostable"), CompostLootItemConditions.MATCH_COMPOSTABLE);
    }

    public static void registerBlockEntityTypes() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "composter"), CompostBlockEntityType.COMPOSTER);
    }

    @Override
    public void onInitialize() {
        registerLootContextKeySets();
        registerLootItemConditionTypes();
        registerBlockEntityTypes();
    }

}