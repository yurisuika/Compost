package dev.yurisuika.compost;

import dev.yurisuika.compost.data.loot.CompostLootTableProvider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

public class CompostData {

    @EventBusSubscriber(modid = Compost.MOD_ID)
    public static class ModEvents {

        public static final RegistrySetBuilder RELOADABLE_BUILDER = new RegistrySetBuilder().add(Registries.LOOT_TABLE, context -> CompostLootTableProvider.create().run(context));

        @SubscribeEvent
        public static void gatherServerData(GatherDataEvent.Server event) {
            event.createReloadableRegistryObjects(RELOADABLE_BUILDER, Set.of(Compost.MOD_ID));
            event.getGenerator().addProvider(true, DatapackBuiltinEntriesProvider.forReloadableLayer(event.getGenerator().getPackOutput(), "Compost Loot Tables", event.getWorldLookupProvider(), event.getReloadableLookupProvider(), RELOADABLE_BUILDER, Set.of(Compost.MOD_ID)));
        }

    }

    public CompostData() {}

}