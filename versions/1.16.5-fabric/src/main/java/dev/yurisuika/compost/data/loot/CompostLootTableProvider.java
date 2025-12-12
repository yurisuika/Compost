package dev.yurisuika.compost.data.loot;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import dev.yurisuika.compost.data.loot.packs.ComposterLoot;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CompostLootTableProvider extends LootTableProvider {

    public final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> subProviders = ImmutableList.of(Pair.of(ComposterLoot::new, CompostLootContextParamSets.COMPOSTER));

    public CompostLootTableProvider(DataGenerator output) {
        super(output);
    }

}