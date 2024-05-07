package dev.yurisuika.compost;

import dev.yurisuika.compost.block.entity.ComposterBlockEntity;
import dev.yurisuika.compost.server.command.CompostCommand;
import dev.yurisuika.compost.util.ConfigUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static dev.yurisuika.compost.server.option.CompostConfig.*;
import static dev.yurisuika.compost.util.NetworkUtil.*;

public class Compost implements ModInitializer {

    public static BlockEntityType<ComposterBlockEntity> COMPOSTER;

    public static void registerBlockEntityTypes() {
        COMPOSTER = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier("compost", "composter"),
                BlockEntityType.Builder.create(ComposterBlockEntity::new, Blocks.COMPOSTER).build(null)
        );
    }

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(CompostCommand::register);
    }

    public static void registerServerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(ConfigUtil::checkWorlds);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sendGroups(handler.player.world, handler.player));
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