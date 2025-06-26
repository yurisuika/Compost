package dev.yurisuika.compost.world;

import java.util.Set;

public class Composition {

    public Compost compost;
    public Set<String> worlds;

    public Composition(Compost compost, Set<String> worlds) {
        this.compost = compost;
        this.worlds = worlds;
    }

    public Compost getCompost() {
        return compost;
    }

    public void setCompost(Compost compost) {
        this.compost = compost;
    }

    public Set<String> getWorlds() {
        return worlds;
    }

    public void setWorlds(Set<String> worlds) {
        this.worlds = worlds;
    }

    public static class Compost {

        public String item;
        public double chance;
        public Count count;

        public Compost(String item, double chance, Count count) {
            this.item = item;
            this.chance = chance;
            this.count = count;
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

        public Count getCount() {
            return count;
        }

        public void setCount(Count count) {
            this.count = count;
        }

        public static class Count {

            public int min;
            public int max;

            public Count(int min, int max) {
                this.min = min;
                this.max = max;
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

    }

}