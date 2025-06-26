package dev.yurisuika.compost;

import dev.yurisuika.compost.commands.arguments.CompositionArgument;
import dev.yurisuika.compost.commands.arguments.CompositionWorldArgument;
import dev.yurisuika.compost.commands.arguments.LoadedWorldArgument;
import dev.yurisuika.compost.network.protocol.common.ClientboundCompostPacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundWorldPacket;
import dev.yurisuika.compost.server.commands.CompostCommand;
import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Validate;
import dev.yurisuika.compost.util.config.Config;
import dev.yurisuika.compost.world.level.block.entity.CompostBlockEntityType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class Compost implements ModInitializer {

    public static void registerBlockEntityTypes() {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, ResourceLocation.tryParse("compost:composter"), CompostBlockEntityType.COMPOSTER);
    }

    public static void registerArgumentTypes() {
        ArgumentTypes.register("compost:composition", CompositionArgument.class, new EmptyArgumentSerializer<>(CompositionArgument::composition));
        ArgumentTypes.register("compost:composition_world", CompositionWorldArgument.class, new EmptyArgumentSerializer<>(CompositionWorldArgument::compositionWorld));
        ArgumentTypes.register("compost:loaded_world", LoadedWorldArgument.class, new EmptyArgumentSerializer<>(LoadedWorldArgument::loadedWorld));
    }

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(CompostCommand::register);
    }

    public static void registerCompositionValidation() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> Validate.validateCompositions());
    }

    public static void registerJoinListeners() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> Network.sendCompositions(handler.player.getLevel(), handler.player));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> Network.setLevelName(server.getWorldData().getLevelName()));
    }

    @Override
    public void onInitialize() {
        Config.loadConfig();

        registerBlockEntityTypes();
        registerArgumentTypes();
        registerCommands();
        registerCompositionValidation();
        registerJoinListeners();
    }

    public static class Client implements ClientModInitializer {

        public static void registerClientReceivers() {
            ClientPlayNetworking.registerGlobalReceiver(ClientboundCompostPacket.ID, ClientboundCompostPacket::handle);
            ClientPlayNetworking.registerGlobalReceiver(ClientboundWorldPacket.ID, ClientboundWorldPacket::handle);
            ClientPlayNetworking.registerGlobalReceiver(ClientboundResetPacket.ID, ClientboundResetPacket::handle);
        }

        @Override
        public void onInitializeClient() {
            registerClientReceivers();
        }

    }

}