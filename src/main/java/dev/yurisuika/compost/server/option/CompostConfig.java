package dev.yurisuika.compost.server.option;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.yurisuika.compost.util.ConfigUtil.*;

public class CompostConfig {

    public static File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "compost.json");
    public static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
    public static Config config = new Config();

    public static class Config {

        public World[] worlds = {
                new World(
                        "world",
                        new World.Group[]{
                                new World.Group("minecraft:dirt", 1.0D, 1, 1),
                                new World.Group("minecraft:bone_meal", 1.0D, 1, 1)
                        }
                )
        };

        public static class World {

            public String world;
            public Group[] items;

            public World(String world, Group[] items) {
                this.world = world;
                this.items = items;
            }

            public static class Group {

                public String item;
                public double chance;
                public int min;
                public int max;

                public Group(String item, double chance, int min, int max) {
                    this.item = item;
                    this.chance = chance;
                    this.min = min;
                    this.max = max;
                }

            }

        }

    }

    public static void saveConfig() {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(getConfig()));
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig() {
        try {
            if (file.exists()) {
                config = gson.fromJson(Files.readString(file.toPath()), Config.class);
            } else {
                config = new Config();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkBounds();
        setConfig(config);
    }

    public static void setConfig(Config config) {
        CompostConfig.config = config;
    }

    public static Config getConfig() {
        return config;
    }

    public static Config.World.Group getGroup(String world, int group) {
        return ArrayUtils.get(ArrayUtils.get(config.worlds, getIndex(world).get()).items, group);
    }

    public static void addGroup(String world, String item, double chance, int min, int max) {
        AtomicInteger index = getIndex(world);
        config.worlds[index.get()].items = ArrayUtils.add(config.worlds[index.get()].items, new Config.World.Group(item, chance, min, max));
        saveConfig();
    }

    public static void removeGroup(String world, int group) {
        AtomicInteger index = getIndex(world);
        config.worlds[index.get()].items = ArrayUtils.remove(config.worlds[index.get()].items, group);
        saveConfig();
    }

}