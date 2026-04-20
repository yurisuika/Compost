package dev.yurisuika.compost.world.level.storage.loot.parameters;

import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class CompostLootContextParamSets {

    public static final ContextKeySet COMPOSTER = new ContextKeySet.Builder().required(LootContextParams.ORIGIN).optional(CompostLootContextParams.COMPOSTABLES).build();

}