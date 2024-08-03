package dev.yurisuika.compost.util.config.options;

import java.util.List;

public class World {

    public String name;
    public List<Produce> produce;

    public World(String name, List<Produce> produce) {
        this.name = name;
        this.produce = produce;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Produce> getProduce() {
        return produce;
    }

    public void setProduce(List<Produce> produce) {
        this.produce = produce;
    }

}