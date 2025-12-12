package dev.yurisuika.compost.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParams;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.List;
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
        return ImmutableSet.of(CompostLootContextParams.COMPOSTABLES);
    }
    @Override
    public boolean test(LootContext context) {
        List<ItemStack> stacks = context.getOptionalParameter(CompostLootContextParams.COMPOSTABLES);

        if (stacks != null) {
            for (ItemStack itemStack : stacks) {
                if (itemStack != null && (predicate.isEmpty() || predicate.get().test(itemStack))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Builder compostableMatches(ItemPredicate.Builder builder) {
        return () -> new MatchCompostable(Optional.of(builder.build()));
    }

}