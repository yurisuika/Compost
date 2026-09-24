package dev.yurisuika.compost;

import dev.yurisuika.compost.data.loot.CompostLootTableProvider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class CompostData {

    @Mod.EventBusSubscriber(modid = Compost.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        public static final RegistrySetBuilder RELOADABLE_BUILDER = new RegistrySetBuilder().add(Registries.LOOT_TABLE, context -> CompostLootTableProvider.create().run(context));

        @SubscribeEvent
        public static void gatherServerData(GatherDataEvent event) {
            event.getGenerator().addProvider(event.includeServer(), RegistriesDatapackGenerator.forReloadableLayer(event.getGenerator().getPackOutput(), event.getLookupProvider()));
        }

    }

    public CompostData() {}

}