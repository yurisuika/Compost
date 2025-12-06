package dev.yurisuika.compost.world.level.storage.loot.parameters;

import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class CompostLootContextParamSets {

    public static final LootContextParamSet COMPOSTER = new LootContextParamSet.Builder().required(LootContextParams.ORIGIN).optional(CompostLootContextParams.COMPOSTABLE).build();

}