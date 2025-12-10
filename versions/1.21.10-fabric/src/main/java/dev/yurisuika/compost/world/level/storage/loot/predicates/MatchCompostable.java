package dev.yurisuika.compost.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParams;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Optional;
import java.util.Set;

public record MatchCompostable(Optional<ItemPredicate> predicate) implements LootItemCondition {

    public static final MapCodec<MatchCompostable> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(MatchCompostable::predicate)).apply(instance, MatchCompostable::new));

    @Override
    public LootItemConditionType getType() {
        return CompostLootItemConditions.MATCH_COMPOSTABLE;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return ImmutableSet.of(CompostLootContextParams.COMPOSTABLE);
    }
    @Override
    public boolean test(LootContext context) {
        ItemStack itemStack = context.getOptionalParameter(CompostLootContextParams.COMPOSTABLE);
        return itemStack != null && (predicate.isEmpty() || predicate.get().test(itemStack));
    }

    public static LootItemCondition.Builder compostableMatches(ItemPredicate.Builder builder) {
        return () -> new MatchCompostable(Optional.of(builder.build()));
    }

}