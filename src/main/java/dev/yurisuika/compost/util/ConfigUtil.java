package dev.yurisuika.compost.util;

import dev.yurisuika.compost.config.CompostConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

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
                StringBuilder contentBuilder = new StringBuilder();
                try (Stream<String> stream = Files.lines(CompostConfig.file.toPath(), StandardCharsets.UTF_8)) {
                    stream.forEach(s -> contentBuilder.append(s).append("\n"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CompostConfig.config = CompostConfig.gson.fromJson(contentBuilder.toString(), CompostConfig.Config.class);
                CompostUtil.checkBounds();
            } else {
                CompostConfig.config = new CompostConfig.Config();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}