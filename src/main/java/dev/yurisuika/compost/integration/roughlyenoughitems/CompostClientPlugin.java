package dev.yurisuika.compost.integration.roughlyenoughitems;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.displays.DefaultCompostingDisplay;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;

import java.util.*;

import static dev.yurisuika.compost.client.option.CompostConfig.*;

@REIPluginClient
public class CompostClientPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        Arrays.stream(config.items).forEach(group -> {
            int page = 0;
            Iterator<List<Object2FloatMap.Entry<ItemConvertible>>> iterator = Iterators.partition(ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.object2FloatEntrySet().stream().sorted(Map.Entry.comparingByValue()).iterator(), 35);
            while (iterator.hasNext()) {
                List<Object2FloatMap.Entry<ItemConvertible>> entries = iterator.next();
                registry.add(DefaultCompostingDisplay.of(entries, Collections.singletonList(EntryIngredients.of(createItemStack(group))), page++));
            }
        });
    }

}