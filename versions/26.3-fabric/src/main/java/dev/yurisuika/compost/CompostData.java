package dev.yurisuika.compost;

import dev.yurisuika.compost.data.loot.CompostLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class CompostData implements DataGeneratorEntrypoint {

    public static final RegistrySetBuilder RELOADABLE_BUILDER = new RegistrySetBuilder().add(Registries.LOOT_TABLE, context -> CompostLootTableProvider.create().run(context));

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        generator.createPack().addProvider(CompostLootTableProvider.Fabric::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
//        registryBuilder.add(Registries.LOOT_TABLE, context -> CompostLootTableProvider.create().run(context));
    }

}