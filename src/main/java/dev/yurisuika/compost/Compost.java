package dev.yurisuika.compost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.yurisuika.compost.block.entity.ComposterBlockEntity;
import dev.yurisuika.compost.server.command.CompostCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Compost implements ModInitializer {

    public static File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "compost.json");
    public static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
    public static Config config = new Config();

    public static class Config {

        public Group[] items = {
            new Group("minecraft:dirt", 1.0D, 1, 1),
            new Group("minecraft:bone_meal", 1.0D, 1, 1)
        };

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
        Compost.config = config;
    }

    public static Config getConfig() {
        return config;
    }

    public static void checkBounds() {
        Arrays.stream(Compost.config.items).forEach(group -> {
            int maxCount = createItemStack(group).getItem().getMaxCount();
            int min = Math.max(Math.min(Math.min(group.min, maxCount), group.max), 0);
            int max = Math.max(Math.max(Math.min(group.max, maxCount), group.min), 1);
            group.chance = Math.max(0.0D, Math.min(group.chance, 1.0D));
            group.min = min;
            group.max = max;
        });
        saveConfig();
    }

    public static ItemStack createItemStack(Group group) {
        int index;
        Item item;
        if (group.item.contains("{")) {
            index = group.item.indexOf("{");
            item = Registries.ITEM.get(new Identifier(group.item.substring(0, index)));
        } else {
            index = 0;
            item = Registries.ITEM.get(new Identifier(group.item));
        }
        ItemStack itemStack = new ItemStack(item, ThreadLocalRandom.current().nextInt(group.min, group.max + 1));
        if (group.item.contains("{")) {
            NbtCompound nbt;
            try {
                nbt = StringNbtReader.parse(group.item.substring(index));
                itemStack.setNbt(nbt);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
        return itemStack;
    }

    public static Group getGroup(int group) {
        return ArrayUtils.get(config.items, group);
    }

    public static void addGroup(String item, double chance, int min, int max) {
        config.items = ArrayUtils.add(config.items, new Group(item, chance, min, max));
        saveConfig();
    }

    public static void removeGroup(int group) {
        config.items = ArrayUtils.remove(config.items, group);
        saveConfig();
    }

    public static BlockEntityType<ComposterBlockEntity> COMPOSTER;

    @Override
    public void onInitialize() {
        if (!file.exists()) {
            saveConfig();
        }
        loadConfig();

        CommandRegistrationCallback.EVENT.register(CompostCommand::register);

        COMPOSTER = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("compost", "composter"),
                FabricBlockEntityTypeBuilder.create(ComposterBlockEntity::new, Blocks.COMPOSTER).build()
        );
    }

}