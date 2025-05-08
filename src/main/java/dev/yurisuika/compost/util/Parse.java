package dev.yurisuika.compost.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.yurisuika.compost.util.config.Option;
import dev.yurisuika.compost.util.config.options.Composition;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Parse {

    public static List<String> listNames(Map<String, Composition> compositions) {
        List<String> names = new ArrayList<>();
        compositions.forEach((name, composition) -> names.add(name));
        return names;
    }

    public static List<String> listLocalNames() {
        return listNames(Option.getCompositions());
    }

    public static List<String> listNetworkNames() {
        return listNames(Network.getNetworkCompositions());
    }

    public static ItemStack createCompost(HolderLookup.Provider provider, Composition composition) {
        try {
            return new ItemArgument(Commands.createValidationContext(provider)).parse(new StringReader(composition.getCompost().getItem())).createItemStack(ThreadLocalRandom.current().nextInt(composition.getCompost().getCount().getMin(), composition.getCompost().getCount().getMax() + 1), true);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ItemStack> createCompostOutput(HolderLookup.Provider provider, Map<String, Composition> compositions, String level, boolean always) {
        List<ItemStack> compost = new ArrayList<>();
        compositions.forEach((name, composition) -> {
            if (composition.getWorlds().isEmpty() || composition.getWorlds().contains(level)) {
                if (always || ThreadLocalRandom.current().nextDouble() < composition.getCompost().getChance()) {
                    compost.add(createCompost(provider, composition));
                }
            }
        });
        return compost;
    }

    public static List<ItemStack> createLocalCompostOutput(HolderLookup.Provider provider, String level) {
        return createCompostOutput(provider, Option.getCompositions(), level, false);
    }

    public static List<ItemStack> createNetworkCompostOutput(HolderLookup.Provider provider, String level) {
        return createCompostOutput(provider, Network.getNetworkCompositions(), level, true);
    }

}