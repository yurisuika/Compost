package com.yurisuika.compost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.GameRuleCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

import static net.minecraft.server.command.CommandManager.*;

public class Compost implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("compost");

    public static File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "compost.json");
    public static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

    public static Config config = new Config();

    public static class Config {

        public boolean shuffle = true;

        public Group[] items = {
                new Group("minecraft:dirt", 1.0F, 1,1),
                new Group("minecraft:bone_meal", 1.0F, 1, 1)
        };

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

    static void loadConfig() {
        try {
            if (file.exists()) {
                config = gson.fromJson(Files.readString(file.toPath()), Config.class);
            } else {
                config = new Config();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setConfig(config);
    }

    public static void setConfig(Config config) {
        Compost.config = config;
    }

    public static Config getConfig() {
        return config;
    }

    public static void setShuffle(boolean bool) {
        config.shuffle = bool;
        saveConfig();
    }

    public static class Group {

        public String item;
        public float chance;
        public int min;
        public int max;

        Group(String item, float chance, int min, int max) {
            this.item = item;
            this.chance = chance;
            this.min = min;
            this.max = max;
        }

    }

    @Override
    public void onInitialize() {
        LOGGER.info("Loading Compost!");

        if (!file.exists()) {
            saveConfig();
        }
        loadConfig();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
                .register(literal("compost")
                .then(literal("shuffle").executes(context -> {
                    boolean bool = config.shuffle;
                    context.getSource().sendFeedback(Text.translatable("compost.commands.shuffle.query", bool), false);
                    return 1;
                })
                .then(argument("shouldShuffle", BoolArgumentType.bool()).executes(context -> {
                    boolean bool = BoolArgumentType.getBool(context, "shouldShuffle");
                    setShuffle(bool);
                    context.getSource().sendFeedback(Text.translatable("compost.commands.shuffle.set", bool), true);
                    return 1;
                }))
                .requires(source -> source.hasPermissionLevel(4)))));
    }

}
