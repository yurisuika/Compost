package dev.yurisuika.compost.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.yurisuika.compost.util.config.Option;
import dev.yurisuika.compost.util.config.options.Produce;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Parse {

    public static List<String> listItems() {
        List<String> items = new ArrayList<>();
        for (Produce produce : Option.getProduce(Validate.getLevelName())) {
            items.add(produce.getItem());
        }
        return items;
    }

    public static ItemStack createItemStack(Produce produce) {
        try {
            return new ItemArgument(Commands.createValidationContext(VanillaRegistries.createLookup())).parse(new StringReader(produce.getItem())).createItemStack(ThreadLocalRandom.current().nextInt(produce.getMin(), produce.getMax() + 1), true);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}