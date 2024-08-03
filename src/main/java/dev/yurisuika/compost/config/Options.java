package dev.yurisuika.compost.config;

import dev.yurisuika.compost.util.config.options.World;

import java.util.ArrayList;
import java.util.List;

public class Options {

    public List<World> worlds = new ArrayList<>();

    public List<World> getWorlds() {
        return worlds;
    }

    public void setWorlds(List<World> worlds) {
        this.worlds = worlds;
    }

}