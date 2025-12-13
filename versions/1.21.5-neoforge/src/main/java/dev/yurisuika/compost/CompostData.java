package dev.yurisuika.compost;

import dev.yurisuika.compost.data.loot.CompostLootTableProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod("compost")
public class CompostData {

    @EventBusSubscriber(modid = "compost", bus = EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void gatherServerData(GatherDataEvent.Server event) {
            event.addProvider(new CompostLootTableProvider(event.getGenerator().getPackOutput(), event.getLookupProvider()));
        }

    }

    public CompostData() {}

}