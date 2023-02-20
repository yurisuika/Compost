package dev.yurisuika.compost.integration.roughlyenoughitems;

import com.google.common.collect.Iterators;
import dev.yurisuika.compost.Compost;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.forge.REIPlugin;
import me.shedaniel.rei.plugin.common.displays.DefaultCompostingDisplay;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraftforge.api.distmarker.Dist;

import java.util.*;

import static dev.yurisuika.compost.Compost.*;

@REIPlugin({Dist.CLIENT})
public class CompostClientPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        Arrays.stream(Compost.config.items).forEach(group -> {
            int page = 0;
            Iterator<List<Object2FloatMap.Entry<ItemConvertible>>> iterator = Iterators.partition(ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.object2FloatEntrySet().stream().sorted(Map.Entry.comparingByValue()).iterator(), 48);
            while (iterator.hasNext()) {
                registry.add(DefaultCompostingDisplay.of(iterator.next(), Collections.singletonList(EntryIngredients.of(createItemStack(group))), page++));
            }
        });
    }

}