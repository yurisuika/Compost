package dev.yurisuika.compost.util;

import dev.yurisuika.compost.config.Config;
import dev.yurisuika.compost.world.Composition;

import java.util.Map;
import java.util.Set;

public class Configure {

    public static Map<String, Composition> getCompositions() {
        return Config.getOptions().getCompositions();
    }

    public static void setCompositions(Map<String, Composition> compositions) {
        Config.getOptions().setCompositions(compositions);
        Config.saveConfig();
    }

    public static Composition getComposition(String name) {
        return getCompositions().get(name);
    }

    public static void setComposition(String name, Composition composition) {
        setItem(name, composition.getCompost().getItem());
        setChance(name, composition.getCompost().getChance());
        setMin(name, composition.getCompost().getCount().getMin());
        setMax(name, composition.getCompost().getCount().getMax());
        setWorlds(name, composition.getWorlds());
        Config.saveConfig();
    }

    public static void addComposition(String name, Composition composition) {
        getCompositions().put(name, composition);
        Config.saveConfig();
    }

    public static void removeComposition(String name) {
        getCompositions().remove(name);
        Config.saveConfig();
    }

    public static void setCompost(String name, Composition.Compost compost) {
        getComposition(name).setCompost(compost);
    }

    public static void setItem(String name, String item) {
        getComposition(name).getCompost().setItem(item);
        Config.saveConfig();
    }

    public static void setChance(String name, Double chance) {
        getComposition(name).getCompost().setChance(chance);
        Config.saveConfig();
    }

    public static void setCount(String name, Composition.Compost.Count count) {
        getComposition(name).getCompost().setCount(count);
        Config.saveConfig();
    }

    public static void setMin(String name, int min) {
        getComposition(name).getCompost().getCount().setMin(min);
        Config.saveConfig();
    }

    public static void setMax(String name, int max) {
        getComposition(name).getCompost().getCount().setMax(max);
        Config.saveConfig();
    }

    public static void setWorlds(String name, Set<String> worlds) {
        getComposition(name).setWorlds(worlds);
        Config.saveConfig();
    }

    public static void addWorld(String name, String world) {
        getComposition(name).getWorlds().add(world);
        Config.saveConfig();
    }

    public static void removeWorld(String name, String world) {
        getComposition(name).getWorlds().remove(world);
        Config.saveConfig();
    }

}