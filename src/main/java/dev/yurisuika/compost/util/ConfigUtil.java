package dev.yurisuika.compost.util;

import dev.yurisuika.compost.config.CompostConfig;

import java.io.FileWriter;
import java.nio.file.Files;

public class ConfigUtil {

    public static void saveConfig() {
        try {
            FileWriter fileWriter = new FileWriter(CompostConfig.file);
            fileWriter.write(CompostConfig.gson.toJson(CompostConfig.config));
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig() {
        try {
            if (CompostConfig.file.exists()) {
                CompostConfig.config = CompostConfig.gson.fromJson(Files.readString(CompostConfig.file.toPath()), CompostConfig.Config.class);
                CompostUtil.checkBounds();
            } else {
                CompostConfig.config = new CompostConfig.Config();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}