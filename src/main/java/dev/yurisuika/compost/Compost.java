package dev.yurisuika.compost;

import dev.yurisuika.compost.commands.arguments.ProduceArgument;
import dev.yurisuika.compost.network.protocol.common.ClientboundProducePacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
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
        public static void registerLevelValidation(ServerStartedEvent event) {
            Validate.checkLevels(event.getServer());
        }

        @SubscribeEvent
        public static void registerJoinPacket(PlayerEvent.PlayerLoggedInEvent event) {
            Network.sendProduce(event.getEntity().level(), event.getEntity());
        }

    }

    @Mod.EventBusSubscriber(modid = "compost", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerBlockEntityTypes(RegisterEvent event) {
            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> helper.register(ResourceLocation.tryParse("compost:composter"), CompostBlockEntityType.COMPOSTER));
        }

        @SubscribeEvent
        public static void registerCommandArgumentTypes(RegisterEvent event) {
            event.register(Registries.COMMAND_ARGUMENT_TYPE, ProduceArgument.ID, () -> ArgumentTypeInfos.registerByClass(ProduceArgument.class, SingletonArgumentInfo.contextFree(ProduceArgument::produce)));
        }

        @SubscribeEvent
        public static void registerPackets(FMLCommonSetupEvent event) {
            ClientboundProducePacket.CHANNEL.messageBuilder(ClientboundProducePacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(ClientboundProducePacket::write).decoder(ClientboundProducePacket::new).consumerMainThread(ClientboundProducePacket::handle).add();
            ClientboundResetPacket.CHANNEL.messageBuilder(ClientboundResetPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(ClientboundResetPacket::write).decoder(ClientboundResetPacket::new).consumerMainThread(ClientboundResetPacket::handle).add();
        }

    }

    public Compost() {
        Config.loadConfig();
    }

}