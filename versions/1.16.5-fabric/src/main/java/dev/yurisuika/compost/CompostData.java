package dev.yurisuika.compost;

import dev.yurisuika.compost.data.loot.CompostLootTableProvider;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.data.DataGenerator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

public class CompostData implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        DataGenerator dataGenerator = new DataGenerator(Paths.get("src/generated/resources"), Collections.emptyList());
        dataGenerator.addProvider(new CompostLootTableProvider(dataGenerator));
        try {
            dataGenerator.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

}