package dev.yurisuika.compost;

import dev.yurisuika.compost.commands.arguments.ProduceArgument;
import dev.yurisuika.compost.network.protocol.common.ClientboundProducePacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
import dev.yurisuika.compost.server.commands.CompostCommand;
import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Validate;
import dev.yurisuika.compost.util.config.Config;
import dev.yurisuika.compost.world.level.block.entity.CompostBlockEntityType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class Compost implements ModInitializer {

    public static void registerBlockEntityTypes() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.tryParse("compost:composter"), CompostBlockEntityType.COMPOSTER);
    }

    public static void registerArgumentTypes() {
        ArgumentTypeRegistry.registerArgumentType(ProduceArgument.ID, ProduceArgument.class, SingletonArgumentInfo.contextFree(ProduceArgument::produce));
    }

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(CompostCommand::register);
    }

    public static void registerLevelValidation() {
        ServerLifecycleEvents.SERVER_STARTED.register(Validate::checkLevels);
    }

    public static void registerJoinPacket() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> Network.sendProduce(handler.getPlayer().level(), handler.getPlayer()));
    }

    @Override
    public void onInitialize() {
        Config.loadConfig();

        registerBlockEntityTypes();
        registerArgumentTypes();
        registerCommands();
        registerLevelValidation();
        registerJoinPacket();
    }

    public static class Client implements ClientModInitializer {

        public static void registerClientReceivers() {
            ClientPlayNetworking.registerGlobalReceiver(ClientboundProducePacket.ID, ClientboundProducePacket::handle);
            ClientPlayNetworking.registerGlobalReceiver(ClientboundResetPacket.ID, ClientboundResetPacket::handle);
        }

        @Override
        public void onInitializeClient() {
            registerClientReceivers();
        }

    }

}