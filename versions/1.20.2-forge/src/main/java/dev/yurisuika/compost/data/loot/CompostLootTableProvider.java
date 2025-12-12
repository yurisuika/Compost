package dev.yurisuika.compost.data.loot;

import dev.yurisuika.compost.data.loot.packs.ComposterLoot;
import dev.yurisuika.compost.world.level.storage.loot.CompostLootTables;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;

import java.util.List;
import java.util.Set;

public class CompostLootTableProvider extends LootTableProvider {

    public CompostLootTableProvider(PackOutput output) {
        super(output, Set.of(CompostLootTables.COMPOSTERS_COMPOST), List.of(new SubProviderEntry(ComposterLoot::new, CompostLootContextParamSets.COMPOSTER)));
    }

}