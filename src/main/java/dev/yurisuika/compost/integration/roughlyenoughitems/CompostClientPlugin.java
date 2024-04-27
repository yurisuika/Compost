package dev.yurisuika.compost.integration.roughlyenoughitems;

import com.google.common.collect.Iterators;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.DefaultCompostingDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ComposterBlock;

import java.util.*;

import static dev.yurisuika.compost.client.option.CompostConfig.*;

@Environment(EnvType.CLIENT)
public class CompostClientPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        EntryIngredient.Builder output = EntryIngredient.builder();

        Arrays.stream(config.items).forEach(group -> {
            output.add(EntryStacks.of(createItemStack(group)));
        });

        Iterator<List<EntryIngredient>> iterator = Iterators.partition(ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.object2FloatEntrySet().stream().sorted(Map.Entry.comparingByValue()).map(entry -> EntryIngredients.of(entry.getKey())).iterator(), 35);
        while (iterator.hasNext()) {
            registry.add(new DefaultCompostingDisplay(iterator.next(), List.of(output.build())));
        }
    }

}