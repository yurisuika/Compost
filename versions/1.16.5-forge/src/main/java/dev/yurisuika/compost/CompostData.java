package dev.yurisuika.compost;

import dev.yurisuika.compost.data.loot.ComposterLootTableProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod("compost")
public class CompostData {

    @Mod.EventBusSubscriber(modid = "compost", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void gatherServerData(GatherDataEvent event) {
            if (event.includeServer()) {
                event.getGenerator().addProvider(new ComposterLootTableProvider(event.getGenerator()));
            }
        }

    }

    public CompostData() {}

}