package dev.yurisuika.compost;

import dev.yurisuika.compost.data.loot.CompostLootTableProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class CompostData {

    @Mod.EventBusSubscriber(modid = Compost.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void gatherServerData(GatherDataEvent event) {
            event.getGenerator().addProvider(event.includeServer(), new CompostLootTableProvider(event.getGenerator().getPackOutput()));
        }

    }

    public CompostData() {}

}