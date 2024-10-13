package dev.yurisuika.compost.util;

import dev.yurisuika.compost.util.config.Config;
import dev.yurisuika.compost.util.config.Option;
import dev.yurisuika.compost.util.config.options.Produce;
import dev.yurisuika.compost.util.config.options.World;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Validate {

    public static String levelName;

    public static String getLevelName() {
        return levelName;
    }

    public static void setLevelName(String levelName) {
        Validate.levelName = levelName;
    }

    public static void checkBounds(Produce produce) {
        int maxStackSize = BuiltInRegistries.ITEM.getValue(ResourceLocation.tryParse(produce.getItem().contains("[") ? produce.getItem().substring(0, produce.getItem().indexOf("[")) : produce.getItem())).getDefaultMaxStackSize();
        int min = Math.max(Math.min(Math.min(produce.getMin(), maxStackSize), produce.getMax()), 0);
        int max = Math.max(Math.max(Math.min(produce.getMax(), maxStackSize), produce.getMin()), 1);
        produce.setChance(Math.max(0.0D, Math.min(produce.getChance(), 1.0D)));
        produce.setMin(min);
        produce.setMax(max);
    }

    public static void checkLevels(MinecraftServer server) {
        AtomicBoolean exists = new AtomicBoolean(false);
        setLevelName(server.getWorldData().getLevelName());
        Option.getWorlds().forEach(world -> {
            world.getProduce().forEach(Validate::checkBounds);
            if (Objects.equals(world.getName(), getLevelName())) {
                exists.set(true);
            }
        });
        if (!exists.get()) {
            List<Produce> produce = new ArrayList<>();
            produce.add(new Produce("minecraft:dirt", 1.0D, 1, 1));
            produce.add(new Produce("minecraft:bone_meal", 1.0D, 1, 1));
            Option.getWorlds().add(new World(getLevelName(), produce));
        }
        Config.saveConfig();
    }

}
