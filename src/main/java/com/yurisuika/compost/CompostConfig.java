package com.yurisuika.compost;

public class CompostConfig {

    public Group[] items = {
            new Group("minecraft:dirt", 1.0F, 1,1),
            new Group("minecraft:bone_meal", 1.0F, 1, 1)
    };

    public static class Group {

        public String item;
        public float chance;
        public int min;
        public int max;

        Group(String item, float chance, int min, int max) {
            this.item = item;
            this.chance = chance;
            this.min = min;
            this.max = max;
        }

    }

}