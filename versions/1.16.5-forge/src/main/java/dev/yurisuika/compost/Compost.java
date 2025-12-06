package dev.yurisuika.compost;

import dev.yurisuika.compost.world.level.block.entity.CompostBlockEntityType;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import dev.yurisuika.compost.world.level.storage.loot.predicates.CompostLootItemConditions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod("compost")
public class Compost {

    @Mod.EventBusSubscriber(modid = "compost", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerLootContextKeySets(FMLCommonSetupEvent event) {
            LootContextParamSets.REGISTRY.put(new ResourceLocation("compost", "composter"), CompostLootContextParamSets.COMPOSTER);
        }

        @SubscribeEvent
        public static void registerLootItemConditionTypes(FMLCommonSetupEvent event) {
            Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation("compost", "match_compostable"), CompostLootItemConditions.MATCH_COMPOSTABLE);
        }

        @SubscribeEvent
        public static void registerBlockEntityTypes(RegistryEvent.Register<BlockEntityType<?>> event) {
            event.getRegistry().register(CompostBlockEntityType.COMPOSTER.setRegistryName(new ResourceLocation("compost", "composter")));
        }

    }

    public Compost() {}

}