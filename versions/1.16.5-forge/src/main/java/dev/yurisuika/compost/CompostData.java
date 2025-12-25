package dev.yurisuika.compost;

import dev.yurisuika.compost.data.loot.CompostLootTableProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class CompostData {

    @Mod.EventBusSubscriber(modid = Compost.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void gatherServerData(GatherDataEvent event) {
            if (event.includeServer()) {
                event.getGenerator().addProvider(new CompostLootTableProvider(event.getGenerator()));
            }
        }

    }

    public CompostData() {}

}