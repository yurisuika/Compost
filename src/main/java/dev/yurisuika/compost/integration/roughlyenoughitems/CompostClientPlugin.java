package dev.yurisuika.compost.integration.roughlyenoughitems;

import com.google.common.collect.Iterators;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.yurisuika.compost.Compost;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.DefaultCompostingDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;

@Environment(EnvType.CLIENT)
public class CompostClientPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        Arrays.stream(Compost.config.items).forEach(group -> {
            Iterator<List<EntryIngredient>> iterator = Iterators.partition(ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.object2FloatEntrySet().stream().sorted(Map.Entry.comparingByValue()).map(entry -> EntryIngredients.of(entry.getKey())).iterator(), 35);
            while (iterator.hasNext()) {
                int index;
                Item item;
                if (group.item.contains("{")) {
                    index = group.item.indexOf("{");
                    item = Registries.ITEM.get(new Identifier(group.item.substring(0, index)));
                } else {
                    index = 0;
                    item = Registries.ITEM.get(new Identifier(group.item));
                }
                ItemStack itemStack = new ItemStack(item);
                if (group.item.contains("{")) {
                    NbtCompound nbt;
                    try {
                        nbt = StringNbtReader.parse(group.item.substring(index));
                        itemStack.setNbt(nbt);
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                }
                List<EntryIngredient> entries = iterator.next();
                registry.add(new DefaultCompostingDisplay(entries, Collections.singletonList(EntryIngredients.of(itemStack))));
            }
        });
    }

}