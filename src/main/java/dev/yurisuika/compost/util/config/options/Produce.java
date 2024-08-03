package dev.yurisuika.compost.util.config.options;

public class Produce {

    public String item;
    public double chance;
    public int min;
    public int max;

    public Produce(String item, double chance, int min, int max) {
        this.item = item;
        this.chance = chance;
        this.min = min;
        this.max = max;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

}