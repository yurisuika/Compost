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
        public static void registerJoinPacket(PlayerEvent.PlayerLoggedInEvent event) {
            Network.sendCompositions(event.getEntity().getLevel(), event.getEntity());
            Network.setLevelName(event.getEntity().getLevel().getServer().getWorldData().getLevelName());
        }

    }

    @Mod.EventBusSubscriber(modid = "compost", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerBlockEntityTypes(RegisterEvent event) {
            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> helper.register(ResourceLocation.tryParse("compost:composter"), CompostBlockEntityType.COMPOSTER));
        }

        @SubscribeEvent
        public static void registerArgumentTypes(RegisterEvent event) {
            event.register(Registries.COMMAND_ARGUMENT_TYPE, ResourceLocation.tryParse("compost:composition"), () -> ArgumentTypeInfos.registerByClass(CompositionArgument.class, SingletonArgumentInfo.contextFree(CompositionArgument::composition)));
            event.register(Registries.COMMAND_ARGUMENT_TYPE, ResourceLocation.tryParse("compost:composition_world"), () -> ArgumentTypeInfos.registerByClass(CompositionWorldArgument.class, SingletonArgumentInfo.contextFree(CompositionWorldArgument::compositionWorld)));
            event.register(Registries.COMMAND_ARGUMENT_TYPE, ResourceLocation.tryParse("compost:loaded_world"), () -> ArgumentTypeInfos.registerByClass(LoadedWorldArgument.class, SingletonArgumentInfo.contextFree(LoadedWorldArgument::loadedWorld)));
        }

        @SubscribeEvent
        public static void registerPackets(FMLCommonSetupEvent event) {
            ClientboundCompostPacket.CHANNEL.registerMessage(1, ClientboundCompostPacket.class, ClientboundCompostPacket::write, ClientboundCompostPacket::new, ClientboundCompostPacket::handle);
            ClientboundWorldPacket.CHANNEL.registerMessage(1, ClientboundWorldPacket.class, ClientboundWorldPacket::write, ClientboundWorldPacket::new, ClientboundWorldPacket::handle);
            ClientboundResetPacket.CHANNEL.registerMessage(1, ClientboundResetPacket.class, ClientboundResetPacket::write, ClientboundResetPacket::new, ClientboundResetPacket::handle);
        }

    }

    public Compost() {
        Config.loadConfig();
    }

}