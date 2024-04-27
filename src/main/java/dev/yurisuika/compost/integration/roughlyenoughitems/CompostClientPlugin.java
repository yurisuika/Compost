package dev.yurisuika.compost.integration.roughlyenoughitems;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.plugin.composting.DefaultCompostingDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static dev.yurisuika.compost.client.option.CompostConfig.*;

@Environment(EnvType.CLIENT)
public class CompostClientPlugin implements REIPluginV0 {

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        List<ItemStack> output = new ArrayList<>();
        Object2FloatMap<ItemConvertible> compostables = ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE;
        int page = 0;

        Arrays.stream(config.items).forEach(group -> {
            output.add(createItemStack(group));
        });

        Iterator<List<Object2FloatMap.Entry<ItemConvertible>>> iterator = Iterators.partition(compostables.object2FloatEntrySet().stream().sorted(Map.Entry.comparingByValue()).iterator(), 48);
        while (iterator.hasNext()) {
            recipeHelper.registerDisplay(new DefaultCompostingDisplay(page, iterator.next(), compostables, output.get(ThreadLocalRandom.current().nextInt(output.size()))));
            page++;
        }
    }

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier("compost", "compost_plugin");
    }

}