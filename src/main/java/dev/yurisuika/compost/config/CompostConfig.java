package dev.yurisuika.compost.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class CompostConfig {

    public static File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "compost.json");
    public static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().disableHtmlEscaping().create();
    public static Config config = new Config();

    public static class Config {

        public Level[] levels = {
                new Level(
                        "world",
                        new Level.Item[]{
                                new Level.Item("minecraft:dirt", 1.0D, 1, 1),
                                new Level.Item("minecraft:bone_meal", 1.0D, 1, 1)
                        }
                )
        };

        public static class Level {

            public String name;
            public Item[] items;

            public Level(String name, Item[] items) {
                this.name = name;
                this.items = items;
            }

            public static class Item {

                public String name;
                public double chance;
                public int min;
                public int max;

                public Item(String name, double chance, int min, int max) {
                    this.name = name;
                    this.chance = chance;
                    this.min = min;
                    this.max = max;
                }

            }

        }

    }

}