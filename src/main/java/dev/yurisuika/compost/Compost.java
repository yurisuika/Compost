package dev.yurisuika.compost;

import dev.yurisuika.compost.commands.arguments.ProduceArgument;
import dev.yurisuika.compost.network.protocol.common.custom.ProducePayload;
import dev.yurisuika.compost.network.protocol.common.custom.ResetPayload;
import dev.yurisuika.compost.server.commands.CompostCommand;
import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Validate;
import dev.yurisuika.compost.util.config.Config;
import dev.yurisuika.compost.world.level.block.entity.CompostBlockEntityType;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
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
        public static void registerLevelValidation(ServerStartedEvent event) {
            Validate.checkLevels(event.getServer());
        }

        @SubscribeEvent
        public static void registerJoinPacket(PlayerEvent.PlayerLoggedInEvent event) {
            Network.sendProduce(event.getEntity().level(), event.getEntity());
        }

    }

    @EventBusSubscriber(modid = "compost", bus = EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerBlockEntityTypes(RegisterEvent event) {
            event.register(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), helper -> helper.register(ResourceLocation.fromNamespaceAndPath("compost", "composter"), CompostBlockEntityType.COMPOSTER));
        }

        @SubscribeEvent
        public static void registerCommandArgumentTypes(RegisterEvent event) {
            event.register(Registries.COMMAND_ARGUMENT_TYPE, ResourceLocation.fromNamespaceAndPath("compost", "produce"), () -> ArgumentTypeInfos.registerByClass(ProduceArgument.class, SingletonArgumentInfo.contextFree(ProduceArgument::produce)));
        }

        @SubscribeEvent
        public static void registerPayloads(RegisterPayloadHandlersEvent event) {
            event.registrar("compost").versioned("1").optional().playToClient(ProducePayload.TYPE, ProducePayload.STREAM_CODEC, ProducePayload::handle);
            event.registrar("compost").versioned("1").optional().playToClient(ResetPayload.TYPE, ResetPayload.STREAM_CODEC, ResetPayload::handle);
        }

    }

    public Compost() {
        Config.loadConfig();
    }

}