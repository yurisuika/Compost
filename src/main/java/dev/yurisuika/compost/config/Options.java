package dev.yurisuika.compost.config;

import dev.yurisuika.compost.util.config.options.Composition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Options {

    public Map<String, Composition> compositions = new HashMap<>(1) {
        {
            put("vanilla", new Composition(new Composition.Compost("minecraft:bone_meal", 1.0D, new Composition.Compost.Count(1, 1)), new HashSet<>()));
            put("compost", new Composition(new Composition.Compost("minecraft:dirt", 1.0D, new Composition.Compost.Count(1, 1)), new HashSet<>()));
        }
    };

    public Map<String, Composition> getCompositions() {
        return compositions;
    }

    public void setCompositions(Map<String, Composition> compositions) {
        this.compositions = compositions;
    }

}