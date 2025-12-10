package dev.yurisuika.compost;

import dev.yurisuika.compost.data.loot.ComposterLootTableProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("compost")
public class CompostData {

    @Mod.EventBusSubscriber(modid = "compost", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void gatherServerData(GatherDataEvent event) {
            event.getGenerator().addProvider(event.includeServer(), new ComposterLootTableProvider(event.getGenerator().getPackOutput()));
        }

    }

    public CompostData() {}

}