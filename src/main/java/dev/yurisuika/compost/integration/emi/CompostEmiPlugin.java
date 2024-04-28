package dev.yurisuika.compost.integration.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.VanillaPlugin;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.registry.EmiTags;
import dev.yurisuika.compost.mixin.mods.EMIInvoker;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EmiEntrypoint
public class CompostEmiPlugin extends VanillaPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(VanillaEmiRecipeCategories.COMPOSTING);
        registry.addWorkstation(VanillaEmiRecipeCategories.COMPOSTING, EmiStack.of(Items.COMPOSTER));

        EMIInvoker.VanillaPluginInvoker.invokeSafely("composting", () -> addComposting(registry, Stream.concat(EmiUtil.values(TagKey.of(EmiPort.getItemRegistry().getKey(), EmiTags.HIDDEN_FROM_RECIPE_VIEWERS)).map(RegistryEntry::value), EmiPort.getDisabledItems()).collect(Collectors.toSet())));
    }

    public static void addComposting(EmiRegistry registry, Set<Item> hiddenItems) {
        EMIInvoker.VanillaPluginInvoker.invokeCompressRecipesToTags(ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.keySet().stream().map(ItemConvertible::asItem).collect(Collectors.toSet()), (a, b) -> Float.compare(ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(a), ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(b)), tag -> {
            registry.addRecipe(new CompostCompostingRecipe(EmiIngredient.of(tag), ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(EmiIngredient.of(tag).getEmiStacks().get(0).getItemStack().getItem()), EMIInvoker.VanillaPluginInvoker.invokeSynthetic("composting/tag", EmiUtil.subId(tag.id()))));
        }, item -> {
            if (!hiddenItems.contains(item)) {
                registry.addRecipe(new CompostCompostingRecipe(EmiStack.of(item), ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(item), EMIInvoker.VanillaPluginInvoker.invokeSynthetic("composting/item", EmiUtil.subId(item))));
            }
        });
    }

}