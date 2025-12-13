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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Compost.MOD_ID)
public class Compost {

    public static final String MOD_ID = "compost";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerLootContextKeySets(FMLCommonSetupEvent event) {
            LootContextParamSets.REGISTRY.put(new ResourceLocation(MOD_ID, "composter"), CompostLootContextParamSets.COMPOSTER);
        }

        @SubscribeEvent
        public static void registerLootItemConditionTypes(FMLCommonSetupEvent event) {
            Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(MOD_ID, "match_compostable"), CompostLootItemConditions.MATCH_COMPOSTABLE);
        }

        @SubscribeEvent
        public static void registerBlockEntityTypes(RegistryEvent.Register<BlockEntityType<?>> event) {
            event.getRegistry().register(CompostBlockEntityType.COMPOSTER.setRegistryName(new ResourceLocation(MOD_ID, "composter")));
        }

    }

    public Compost() {}

}