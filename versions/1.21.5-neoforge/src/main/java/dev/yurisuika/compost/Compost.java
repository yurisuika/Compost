package dev.yurisuika.compost;

import dev.yurisuika.compost.world.level.block.entity.CompostBlockEntityType;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import dev.yurisuika.compost.world.level.storage.loot.predicates.CompostLootItemConditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Compost.MOD_ID)
public class Compost {

    public static final String MOD_ID = "compost";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerLootContextKeySets(RegisterEvent event) {
            LootContextParamSets.REGISTRY.put(ResourceLocation.fromNamespaceAndPath(MOD_ID, "composter"), CompostLootContextParamSets.COMPOSTER);
        }

        @SubscribeEvent
        public static void registerLootItemConditionTypes(RegisterEvent event) {
            event.register(BuiltInRegistries.LOOT_CONDITION_TYPE.key(), helper -> helper.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "match_compostable"), CompostLootItemConditions.MATCH_COMPOSTABLE));
        }

        @SubscribeEvent
        public static void registerBlockEntityTypes(RegisterEvent event) {
            event.register(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), helper -> helper.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "composter"), CompostBlockEntityType.COMPOSTER));
        }

    }

    public Compost() {}

}