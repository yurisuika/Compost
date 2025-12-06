package dev.yurisuika.compost.world.level.storage.loot.predicates;

import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class CompostLootItemConditions {

    public static final LootItemConditionType MATCH_COMPOSTABLE = new LootItemConditionType(new MatchCompostable.Serializer());

}