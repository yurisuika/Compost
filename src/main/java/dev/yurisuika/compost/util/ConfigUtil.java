package dev.yurisuika.compost.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.yurisuika.compost.server.option.CompostConfig;
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
import java.util.concurrent.atomic.AtomicInteger;

import static dev.yurisuika.compost.server.option.CompostConfig.*;

public class ConfigUtil {

    public static void checkBounds() {
        Arrays.stream(config.worlds).forEach(level -> Arrays.stream(level.items).forEach(group -> {
            int maxCount = (group.item.contains("[") ? Registries.ITEM.get(new Identifier(group.item.substring(0, group.item.indexOf("[")))) : Registries.ITEM.get(new Identifier(group.item))).getMaxCount();
            int min = Math.max(Math.min(Math.min(group.min, maxCount), group.max), 0);
            int max = Math.max(Math.max(Math.min(group.max, maxCount), group.min), 1);
            group.chance = Math.max(0.0D, Math.min(group.chance, 1.0D));
            group.min = min;
            group.max = max;
        }));
        saveConfig();
    }

    public static void checkWorlds(MinecraftServer server) {
        AtomicBoolean exists = new AtomicBoolean(false);
        String world = server.getSaveProperties().getLevelName();
        Arrays.stream(config.worlds).forEach(level -> {
            if (Objects.equals(level.world, world)) {
                exists.set(true);
            }
        });
        if (!exists.get()) {
            config.worlds = ArrayUtils.add(config.worlds, new CompostConfig.Config.World(world, new CompostConfig.Config.World.Group[]{
                    new CompostConfig.Config.World.Group("minecraft:dirt", 1.0D, 1, 1),
                    new CompostConfig.Config.World.Group("minecraft:bone_meal", 1.0D, 1, 1)
            }));
        }
        saveConfig();
    }

    public static AtomicInteger getIndex(String world) {
        AtomicInteger index = new AtomicInteger();
        Arrays.stream(config.worlds).forEach(level -> {
            if (Objects.equals(level.world, world)) {
                index.set(ArrayUtils.indexOf(config.worlds, level));
            }
        });
        return index;
    }

    public static ItemStack createItemStack(CompostConfig.Config.World.Group group) {
        try {
            return new ItemStackArgumentType(CommandManager.createRegistryAccess(BuiltinRegistries.createWrapperLookup())).parse(new StringReader(group.item)).createStack(ThreadLocalRandom.current().nextInt(group.min, group.max + 1), true);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}