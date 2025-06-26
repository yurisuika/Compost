package dev.yurisuika.compost.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.yurisuika.compost.world.Composition;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Parse {

    public static ItemStack createItemStack(Composition composition) {
        try {
            return new ItemArgument().parse(new StringReader(composition.getCompost().getItem())).createItemStack(ThreadLocalRandom.current().nextInt(composition.getCompost().getCount().getMin(), composition.getCompost().getCount().getMax() + 1), true);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ItemStack> createCompost(Map<String, Composition> compositions, String level, boolean always) {
        List<ItemStack> compost = new ArrayList<>();
        compositions.forEach((name, composition) -> {
            if (composition.getWorlds().isEmpty() || composition.getWorlds().contains(level)) {
                if (always || ThreadLocalRandom.current().nextDouble() < composition.getCompost().getChance()) {
                    compost.add(createItemStack(composition));
                }
            }
        });
        return compost;
    }

    public static List<ItemStack> createLocalCompost(String level) {
        return createCompost(Configure.getCompositions(), level, false);
    }

    public static List<ItemStack> createNetworkCompost(String level) {
        return createCompost(Network.COMPOSITIONS, level, true);
    }

}