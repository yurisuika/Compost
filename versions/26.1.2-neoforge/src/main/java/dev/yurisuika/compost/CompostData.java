package dev.yurisuika.compost;

import dev.yurisuika.compost.data.loot.CompostLootTableProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class CompostData {

    @EventBusSubscriber(modid = Compost.MOD_ID)
    public static class ModEvents {

        @SubscribeEvent
        public static void gatherServerData(GatherDataEvent.Server event) {
            event.addProvider(new CompostLootTableProvider(event.getGenerator().getPackOutput(), event.getLookupProvider()));
        }

    }

    public CompostData() {}

}