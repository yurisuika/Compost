package dev.yurisuika.compost.data.loot;

import dev.yurisuika.compost.data.loot.packs.ComposterLoot;
import dev.yurisuika.compost.world.level.storage.loot.CompostLootTables;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public class CompostLootTableProvider extends SimpleFabricLootTableProvider {

    public CompostLootTableProvider(FabricDataGenerator generator) {
        super(generator, CompostLootContextParamSets.COMPOSTER);
    }

    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> output) {
        output.accept(CompostLootTables.COMPOSTERS_COMPOST, ComposterLoot.COMPOST);
    }

}