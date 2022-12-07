package com.yurisuika.compost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

public class Compost implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("compost");

    public static File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "compost.json");
    public static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

    public static CompostConfig config = new CompostConfig();

    public static void saveConfig() {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(getConfig()));
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void loadConfig() {
        try {
            if (file.exists()) {
                config = gson.fromJson(Files.readString(file.toPath()), CompostConfig.class);
            } else {
                config = new CompostConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setConfig(config);
    }

    public static void setConfig(CompostConfig config) {
        Compost.config = config;
    }

    public static CompostConfig getConfig() {
        return config;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Loading Compost!");

        if (!file.exists()) {
            saveConfig();
        }
        loadConfig();
    }

}
