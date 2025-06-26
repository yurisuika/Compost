package dev.yurisuika.compost;

import dev.yurisuika.compost.commands.arguments.CompositionArgument;
import dev.yurisuika.compost.commands.arguments.CompositionWorldArgument;
import dev.yurisuika.compost.commands.arguments.LoadedWorldArgument;
import dev.yurisuika.compost.config.Config;
import dev.yurisuika.compost.network.protocol.common.custom.CompostPayload;
import dev.yurisuika.compost.network.protocol.common.custom.ResetPayload;
import dev.yurisuika.compost.network.protocol.common.custom.WorldPayload;
import dev.yurisuika.compost.server.commands.CompostCommand;
import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Validate;
import dev.yurisuika.compost.world.level.block.entity.CompostBlockEntityType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class Compost implements ModInitializer {

    public static void registerBlockEntityTypes() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("compost", "composter"), CompostBlockEntityType.COMPOSTER);
    }

    public static void registerArgumentTypes() {
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath("compost", "composition"), CompositionArgument.class, SingletonArgumentInfo.contextFree(CompositionArgument::composition));
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath("compost", "composition_world"), CompositionWorldArgument.class, SingletonArgumentInfo.contextFree(CompositionWorldArgument::compositionWorld));
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath("compost", "loaded_world"), LoadedWorldArgument.class, SingletonArgumentInfo.contextFree(LoadedWorldArgument::loadedWorld));
    }

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(CompostCommand::register);
    }

    public static void registerCompositionValidation() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> Validate.validateCompositions());
    }

    public static void registerJoinListeners() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> Network.sendCompositions(handler.getPlayer().level(), handler.getPlayer()));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> Network.setLevelName(server.getWorldData().getLevelName()));
    }

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(CompostPayload.TYPE, CompostPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(WorldPayload.TYPE, WorldPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ResetPayload.TYPE, ResetPayload.STREAM_CODEC);
    }

    @Override
    public void onInitialize() {
        Config.loadConfig();

        registerBlockEntityTypes();
        registerArgumentTypes();
        registerCommands();
        registerCompositionValidation();
        registerJoinListeners();
        registerPayloads();
    }

    public static class Client implements ClientModInitializer {

        public static void registerClientReceivers() {
            ClientPlayNetworking.registerGlobalReceiver(CompostPayload.TYPE, CompostPayload::handle);
            ClientPlayNetworking.registerGlobalReceiver(WorldPayload.TYPE, WorldPayload::handle);
            ClientPlayNetworking.registerGlobalReceiver(ResetPayload.TYPE, ResetPayload::handle);
        }

        @Override
        public void onInitializeClient() {
            registerClientReceivers();
        }

    }

}