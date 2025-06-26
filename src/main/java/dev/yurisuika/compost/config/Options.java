package dev.yurisuika.compost.config;

import dev.yurisuika.compost.world.Composition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Options {

    public Map<String, Composition> compositions = new HashMap<>(2) {
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