package dev.yurisuika.compost;

import dev.yurisuika.compost.commands.arguments.ProduceArgument;
import dev.yurisuika.compost.network.protocol.common.custom.ProducePayload;
import dev.yurisuika.compost.network.protocol.common.custom.ResetPayload;
import dev.yurisuika.compost.server.commands.CompostCommand;
import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Validate;
import dev.yurisuika.compost.util.config.Config;
import dev.yurisuika.compost.world.level.block.entity.ContainerComposterBlockEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class Compost implements ModInitializer {

    public static BlockEntityType<ContainerComposterBlockEntity> COMPOSTER;

    public static void registerBlockEntityTypes() {
        COMPOSTER = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.tryParse("compost:composter"), FabricBlockEntityTypeBuilder.create(ContainerComposterBlockEntity::new, Blocks.COMPOSTER).build());
    }

    public static void registerServerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(Validate::checkLevels);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> Network.sendProduce(handler.getPlayer().level(), handler.getPlayer()));
    }

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(CompostCommand::register);
    }

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(ProducePayload.TYPE, ProducePayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ResetPayload.TYPE, ResetPayload.STREAM_CODEC);
    }

    @Override
    public void onInitialize() {
        Config.loadConfig();

        registerBlockEntityTypes();
        registerServerEvents();
        registerCommands();
        registerPayloads();
    }

    public static class Client implements ClientModInitializer {

        public static void registerArgumentTypes() {
            ArgumentTypeRegistry.registerArgumentType(ResourceLocation.tryParse("compost:produce"), ProduceArgument.class, SingletonArgumentInfo.contextFree(ProduceArgument::produce));
        }

        public static void registerGlobalReceivers() {
            ClientPlayNetworking.registerGlobalReceiver(ProducePayload.TYPE, ProducePayload::handle);
            ClientPlayNetworking.registerGlobalReceiver(ResetPayload.TYPE, ResetPayload::handle);
        }

        @Override
        public void onInitializeClient() {
            registerArgumentTypes();
            registerGlobalReceivers();
        }

    }

}