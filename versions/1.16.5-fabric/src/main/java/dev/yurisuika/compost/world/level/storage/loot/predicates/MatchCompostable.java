package dev.yurisuika.compost.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParams;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.List;
import java.util.Set;

public class MatchCompostable implements LootItemCondition {

    public ItemPredicate predicate;

    public MatchCompostable(ItemPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public LootItemConditionType getType() {
        return CompostLootItemConditions.MATCH_COMPOSTABLE;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(CompostLootContextParams.COMPOSTABLES);
    }

    @Override
    public boolean test(LootContext context) {
        List<ItemStack> stacks = context.getParamOrNull(CompostLootContextParams.COMPOSTABLES);

        if (stacks != null) {
            for (ItemStack itemStack : stacks) {
                if (itemStack != null && predicate.matches(itemStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Builder compostableMatches(ItemPredicate.Builder builder) {
        return () -> new MatchCompostable(builder.build());
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<MatchCompostable> {

        @Override
        public void serialize(JsonObject json, MatchCompostable compostable, JsonSerializationContext context) {
            json.add("predicate", compostable.predicate.serializeToJson());
        }

        @Override
        public MatchCompostable deserialize(JsonObject json, JsonDeserializationContext context) {
            return new MatchCompostable(ItemPredicate.fromJson(json.get("predicate")));
        }

    }

}