package dev.yurisuika.compost;

import dev.yurisuika.compost.commands.arguments.ProduceArgument;
import dev.yurisuika.compost.network.protocol.common.ClientboundProducePacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
import dev.yurisuika.compost.server.commands.CompostCommand;
import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Validate;
import dev.yurisuika.compost.util.config.Config;
import dev.yurisuika.compost.world.level.block.entity.CompostBlockEntityType;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fmlserverevents.FMLServerStartedEvent;

@Mod("compost")
public class Compost {

    @Mod.EventBusSubscriber(modid = "compost", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class GameEvents {

        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            CompostCommand.register(event.getDispatcher(), event.getEnvironment());
        }

        @SubscribeEvent
        public static void registerLevelValidation(FMLServerStartedEvent event) {
            Validate.checkLevels(event.getServer());
        }

        @SubscribeEvent
        public static void registerJoinPacket(PlayerEvent.PlayerLoggedInEvent event) {
            Network.sendProduce(event.getPlayer().getCommandSenderWorld(), event.getPlayer());
        }

    }

    @Mod.EventBusSubscriber(modid = "compost", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerBlockEntityTypes(RegistryEvent.Register<BlockEntityType<?>> event) {
            event.getRegistry().register(CompostBlockEntityType.COMPOSTER.setRegistryName(ResourceLocation.tryParse("compost:composter")));
        }

        @SubscribeEvent
        public static void registerArgumentTypes(FMLCommonSetupEvent event) {
            ArgumentTypes.register("compost:produce", ProduceArgument.class, new EmptyArgumentSerializer<>(ProduceArgument::produce));
        }

        @SubscribeEvent
        public static void registerPackets(FMLCommonSetupEvent event) {
            ClientboundProducePacket.CHANNEL.registerMessage(1, ClientboundProducePacket.class, ClientboundProducePacket::write, ClientboundProducePacket::new, ClientboundProducePacket::handle);
            ClientboundResetPacket.CHANNEL.registerMessage(1, ClientboundResetPacket.class, ClientboundResetPacket::write, ClientboundResetPacket::new, ClientboundResetPacket::handle);
        }

    }

    public Compost() {
        Config.loadConfig();
    }

}