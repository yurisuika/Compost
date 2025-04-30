package dev.yurisuika.compost.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.yurisuika.compost.util.config.Option;
import dev.yurisuika.compost.util.config.options.Composition;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.RegistryAccess;
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

    public static ItemStack createCompost(Composition composition) {
        try {
            return new ItemArgument(new CommandBuildContext(RegistryAccess.BUILTIN.get())).parse(new StringReader(composition.getCompost().getItem())).createItemStack(ThreadLocalRandom.current().nextInt(composition.getCompost().getCount().getMin(), composition.getCompost().getCount().getMax() + 1), true);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ItemStack> createCompostOutput(Map<String, Composition> compositions, String level) {
        List<ItemStack> compost = new ArrayList<>();
        compositions.forEach((name, composition) -> {
            if (composition.getWorlds().isEmpty() || composition.getWorlds().contains(level)) {
                compost.add(createCompost(composition));
            }
        });
        return compost;
    }

    public static List<ItemStack> createLocalCompostOutput(String level) {
        return createCompostOutput(Option.getCompositions(), level);
    }

    public static List<ItemStack> createNetworkCompostOutput(String level) {
        return createCompostOutput(Network.getNetworkCompositions(), level);
    }

}