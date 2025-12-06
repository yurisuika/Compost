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

@Mod("compost")
public class Compost {

    @Mod.EventBusSubscriber(modid = "compost", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerLootContextKeySets(RegisterEvent event) {
            LootContextParamSets.REGISTRY.put(new ResourceLocation("compost", "composter"), CompostLootContextParamSets.COMPOSTER);
        }

        @SubscribeEvent
        public static void registerLootItemConditionTypes(RegisterEvent event) {
            event.register(Registry.LOOT_CONDITION_TYPE.key(),helper -> helper.register(new ResourceLocation("compost", "match_compostable"), CompostLootItemConditions.MATCH_COMPOSTABLE));
        }

        @SubscribeEvent
        public static void registerBlockEntityTypes(RegisterEvent event) {
            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> helper.register(new ResourceLocation("compost", "composter"), CompostBlockEntityType.COMPOSTER));
        }

    }

    public Compost() {}

}