package dev.yurisuika.compost;

import dev.yurisuika.compost.world.level.block.entity.CompostBlockEntityType;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import dev.yurisuika.compost.world.level.storage.loot.predicates.CompostLootItemConditions;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Compost implements ModInitializer {

    public static final String MOD_ID = "compost";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void registerLootContextKeySets() {
        LootContextParamSets.REGISTRY.put(new ResourceLocation(MOD_ID, "composter"), CompostLootContextParamSets.COMPOSTER);
    }

    public static void registerLootItemConditionTypes() {
        Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(MOD_ID, "match_compostable"), CompostLootItemConditions.MATCH_COMPOSTABLE);
    }

    public static void registerBlockEntityTypes() {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(MOD_ID, "composter"), CompostBlockEntityType.COMPOSTER);
    }

    @Override
    public void onInitialize() {
        registerLootContextKeySets();
        registerLootItemConditionTypes();
        registerBlockEntityTypes();
    }

}