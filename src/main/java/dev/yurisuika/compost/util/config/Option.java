package dev.yurisuika.compost.util.config;

import dev.yurisuika.compost.util.config.options.Produce;
import dev.yurisuika.compost.util.config.options.World;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Option {

    public static List<World> getWorlds() {
        return Config.getOptions().getWorlds();
    }

    public static void setWorlds(List<World> worlds) {
        Config.getOptions().setWorlds(worlds);
        Config.saveConfig();
    }

    public static World getWorld(String name) {
        AtomicReference<World> entry = new AtomicReference<>();
        Option.getWorlds().forEach(value -> {
            if (Objects.equals(value.getName(), name)) {
                entry.set(value);
            }
        });
        return entry.get();
    }

    public static void setWorld(String name, World world) {
        AtomicReference<World> entry = new AtomicReference<>();
        Option.getWorlds().forEach(value -> {
            if (Objects.equals(value.getName(), name)) {
                entry.set(value);
            }
        });
        entry.set(world);
    }

    public static List<Produce> getProduce(String name) {
        return getWorld(name).getProduce();
    }

    public static void setProduce(String name, List<Produce> produce) {
        getWorld(name).setProduce(produce);
        Config.saveConfig();
    }

    public static void addProduce(String name, Produce produce) {
        getProduce(name).add(produce);
        Config.saveConfig();
    }

    public static void removeProduce(String name, Produce produce) {
        getProduce(name).remove(produce);
        Config.saveConfig();
    }

}