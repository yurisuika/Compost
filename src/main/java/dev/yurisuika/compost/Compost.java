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
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod("compost")
public class Compost {

    @EventBusSubscriber(modid = "compost", bus = EventBusSubscriber.Bus.GAME)
    public static class GameEvents {

        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            CompostCommand.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }

        @SubscribeEvent
        public static void registerCompositionValidation(ServerStartedEvent event) {
            Validate.validateCompositions();
        }

        @SubscribeEvent
        public static void registerJoinListeners(PlayerEvent.PlayerLoggedInEvent event) {
            Network.sendCompositions(event.getEntity().level(), (ServerPlayer) event.getEntity());
            Network.setLevelName(event.getEntity().level().getServer().getWorldData().getLevelName());
        }

    }

    @EventBusSubscriber(modid = "compost", bus = EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerBlockEntityTypes(RegisterEvent event) {
            event.register(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), helper -> helper.register(ResourceLocation.fromNamespaceAndPath("compost", "composter"), CompostBlockEntityType.COMPOSTER));
        }

        @SubscribeEvent
        public static void registerArgumentTypes(RegisterEvent event) {
            event.register(Registries.COMMAND_ARGUMENT_TYPE, ResourceLocation.fromNamespaceAndPath("compost", "composition"), () -> ArgumentTypeInfos.registerByClass(CompositionArgument.class, SingletonArgumentInfo.contextFree(CompositionArgument::composition)));
            event.register(Registries.COMMAND_ARGUMENT_TYPE, ResourceLocation.fromNamespaceAndPath("compost", "composition_world"), () -> ArgumentTypeInfos.registerByClass(CompositionWorldArgument.class, SingletonArgumentInfo.contextFree(CompositionWorldArgument::compositionWorld)));
            event.register(Registries.COMMAND_ARGUMENT_TYPE, ResourceLocation.fromNamespaceAndPath("compost", "loaded_world"), () -> ArgumentTypeInfos.registerByClass(LoadedWorldArgument.class, SingletonArgumentInfo.contextFree(LoadedWorldArgument::loadedWorld)));
        }

        @SubscribeEvent
        public static void registerPayloads(RegisterPayloadHandlersEvent event) {
            event.registrar("compost").versioned("1").optional().playToClient(CompostPayload.TYPE, CompostPayload.STREAM_CODEC, CompostPayload::handle);
            event.registrar("compost").versioned("1").optional().playToClient(WorldPayload.TYPE, WorldPayload.STREAM_CODEC, WorldPayload::handle);
            event.registrar("compost").versioned("1").optional().playToClient(ResetPayload.TYPE, ResetPayload.STREAM_CODEC, ResetPayload::handle);
        }

    }

    public Compost() {
        Config.loadConfig();
    }

}