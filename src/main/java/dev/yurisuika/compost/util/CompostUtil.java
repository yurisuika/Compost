package dev.yurisuika.compost.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static dev.yurisuika.compost.config.CompostConfig.*;

public class CompostUtil {

    public static Config.Level getLevel(String name) {
        AtomicReference<Config.Level> output = new AtomicReference<>();
        Arrays.stream(config.levels).forEach(level -> {
            if (Objects.equals(level.name, name)) {
                output.set(level);
            }
        });
        return output.get();
    }

    public static void addItem(String name, Config.Level.Item item) {
        getLevel(name).items = ArrayUtils.add(getLevel(name).items, item);
        ConfigUtil.saveConfig();
    }

    public static void removeItem(String name, int index) {
        getLevel(name).items = ArrayUtils.remove(getLevel(name).items, index);
        ConfigUtil.saveConfig();
    }

    public static void checkBounds() {
        Arrays.stream(config.levels).forEach(level -> Arrays.stream(level.items).forEach(item -> {
            int maxCount = (item.name.contains("[") ? Registries.ITEM.get(new Identifier(item.name.substring(0, item.name.indexOf("[")))) : Registries.ITEM.get(new Identifier(item.name))).getMaxCount();
            int min = Math.max(Math.min(Math.min(item.min, maxCount), item.max), 0);
            int max = Math.max(Math.max(Math.min(item.max, maxCount), item.min), 1);
            item.chance = Math.max(0.0D, Math.min(item.chance, 1.0D));
            item.min = min;
            item.max = max;
        }));
        ConfigUtil.saveConfig();
    }

    public static void checkLevels(MinecraftServer server) {
        AtomicBoolean exists = new AtomicBoolean(false);
        String name = server.getSaveProperties().getLevelName();
        Arrays.stream(config.levels).forEach(level -> {
            if (Objects.equals(level.name, name)) {
                exists.set(true);
            }
        });
        if (!exists.get()) {
            config.levels = ArrayUtils.add(config.levels, new Config.Level(name, new Config.Level.Item[]{
                    new Config.Level.Item("minecraft:dirt", 1.0D, 1, 1),
                    new Config.Level.Item("minecraft:bone_meal", 1.0D, 1, 1)
            }));
        }
        ConfigUtil.saveConfig();
    }

    public static ItemStack createItemStack(Config.Level.Item item) {
        try {
            return new ItemStackArgumentType(CommandManager.createRegistryAccess(BuiltinRegistries.createWrapperLookup())).parse(new StringReader(item.name)).createStack(ThreadLocalRandom.current().nextInt(item.min, item.max + 1), true);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}