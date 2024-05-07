package dev.yurisuika.compost;

import dev.yurisuika.compost.block.entity.ComposterBlockEntity;
import dev.yurisuika.compost.util.ConfigUtil;
import dev.yurisuika.compost.server.command.CompostCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static dev.yurisuika.compost.server.option.CompostConfig.*;
import static dev.yurisuika.compost.util.NetworkUtil.*;

public class Compost implements ModInitializer {

    public static BlockEntityType<ComposterBlockEntity> COMPOSTER;

    public static void registerBlockEntityTypes() {
        COMPOSTER = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("compost", "composter"),
                FabricBlockEntityTypeBuilder.create(ComposterBlockEntity::new, Blocks.COMPOSTER).build()
        );
    }

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(CompostCommand::register);
    }

    public static void registerServerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(ConfigUtil::checkWorlds);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sendGroups(handler.getPlayer().getWorld(), handler.getPlayer()));
    }

    @Override
    public void onInitialize() {
        if (!file.exists()) {
            saveConfig();
        }
        loadConfig();

        registerBlockEntityTypes();
        registerCommands();
        registerServerEvents();
    }

}