package dev.yurisuika.compost;

import dev.yurisuika.compost.world.level.block.entity.CompostBlockEntityType;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import dev.yurisuika.compost.world.level.storage.loot.predicates.CompostLootItemConditions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Compost.MOD_ID)
public class Compost {

    public static final String MOD_ID = "compost";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerLootContextKeySets(RegisterEvent event) {
            LootContextParamSets.REGISTRY.put(new ResourceLocation(MOD_ID, "composter"), CompostLootContextParamSets.COMPOSTER);
        }

        @SubscribeEvent
        public static void registerLootItemConditionTypes(RegisterEvent event) {
            event.register(Registry.LOOT_CONDITION_TYPE.key(),helper -> helper.register(new ResourceLocation(MOD_ID, "match_compostable"), CompostLootItemConditions.MATCH_COMPOSTABLE));
        }

        @SubscribeEvent
        public static void registerBlockEntityTypes(RegisterEvent event) {
            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> helper.register(new ResourceLocation(MOD_ID, "composter"), CompostBlockEntityType.COMPOSTER));
        }

    }

    public Compost() {}

}