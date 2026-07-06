package dev.yurisuika.compost.world.level.storage.loot.parameters;

import dev.yurisuika.compost.Compost;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CompostLootContextParams {

    public static final ContextKey<List<ItemStack>> COMPOSTABLES = new ContextKey<>(Identifier.fromNamespaceAndPath(Compost.MOD_ID, "compostables"));

}