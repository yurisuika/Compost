package dev.yurisuika.compost.util;

import dev.yurisuika.compost.util.config.Config;
import dev.yurisuika.compost.util.config.Option;
import dev.yurisuika.compost.util.config.options.Composition;

public class Validate {

    public static void validateComposition(String name) {
        validateComposition(Option.getComposition(name));
    }

    public static void validateComposition(Composition composition) {
        double chance = Math.max(0.0D, Math.min(composition.getCompost().getChance(), 1.0D));
        int min = Math.max(Math.min(Math.min(composition.getCompost().getCount().getMin(), composition.getCompost().getCount().getMax()), 64), 1);
        int max = Math.max(Math.min(Math.max(composition.getCompost().getCount().getMax(), composition.getCompost().getCount().getMin()), 64), 1);
        composition.getCompost().setChance(chance);
        composition.getCompost().getCount().setMin(min);
        composition.getCompost().getCount().setMax(max);
        Config.saveConfig();
    }

    public static void validateCompositions() {
        Option.getCompositions().forEach((name, composition) -> validateComposition(composition));
    }

}