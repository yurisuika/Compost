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
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod("compost")
public class Compost {

    @Mod.EventBusSubscriber(modid = "compost", bus = Mod.EventBusSubscriber.Bus.FORGE)
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
            Network.sendCompositions(event.getEntity().level(), event.getEntity());
            Network.setLevelName(event.getEntity().level().getServer().getWorldData().getLevelName());
        }

    }

    @Mod.EventBusSubscriber(modid = "compost", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerBlockEntityTypes(RegisterEvent event) {
            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> helper.register(ResourceLocation.fromNamespaceAndPath("compost", "composter"), CompostBlockEntityType.COMPOSTER));
        }

        @SubscribeEvent
        public static void registerArgumentTypes(RegisterEvent event) {
            event.register(Registries.COMMAND_ARGUMENT_TYPE, ResourceLocation.fromNamespaceAndPath("compost", "composition"), () -> ArgumentTypeInfos.registerByClass(CompositionArgument.class, SingletonArgumentInfo.contextFree(CompositionArgument::composition)));
            event.register(Registries.COMMAND_ARGUMENT_TYPE, ResourceLocation.fromNamespaceAndPath("compost", "composition_world"), () -> ArgumentTypeInfos.registerByClass(CompositionWorldArgument.class, SingletonArgumentInfo.contextFree(CompositionWorldArgument::compositionWorld)));
            event.register(Registries.COMMAND_ARGUMENT_TYPE, ResourceLocation.fromNamespaceAndPath("compost", "loaded_world"), () -> ArgumentTypeInfos.registerByClass(LoadedWorldArgument.class, SingletonArgumentInfo.contextFree(LoadedWorldArgument::loadedWorld)));
        }

        @SubscribeEvent
        public static void registerPackets(FMLCommonSetupEvent event) {
            ClientboundCompostPacket.CHANNEL.messageBuilder(ClientboundCompostPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(ClientboundCompostPacket::write).decoder(ClientboundCompostPacket::new).consumerMainThread(ClientboundCompostPacket::handle).add();
            ClientboundWorldPacket.CHANNEL.messageBuilder(ClientboundWorldPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(ClientboundWorldPacket::write).decoder(ClientboundWorldPacket::new).consumerMainThread(ClientboundWorldPacket::handle).add();
            ClientboundResetPacket.CHANNEL.messageBuilder(ClientboundResetPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(ClientboundResetPacket::write).decoder(ClientboundResetPacket::new).consumerMainThread(ClientboundResetPacket::handle).add();
        }

    }

    public Compost() {
        Config.loadConfig();
    }

}