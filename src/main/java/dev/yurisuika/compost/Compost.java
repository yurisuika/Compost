package dev.yurisuika.compost;

import dev.yurisuika.compost.commands.arguments.ProduceArgument;
import dev.yurisuika.compost.network.protocol.common.ClientboundProducePacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
import dev.yurisuika.compost.server.commands.CompostCommand;
import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Validate;
import dev.yurisuika.compost.util.config.Config;
import dev.yurisuika.compost.world.level.block.entity.ContainerComposterBlockEntity;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod("compost")
public class Compost {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "compost");

    public static final RegistryObject<BlockEntityType<ContainerComposterBlockEntity>> COMPOSTER = BLOCK_ENTITIES.register("composter", () -> BlockEntityType.Builder.of(ContainerComposterBlockEntity::new, Blocks.COMPOSTER).build(null));

    @Mod.EventBusSubscriber(modid = "compost")
    public static class CommonForgeEvents {

        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            CompostCommand.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }

        @SubscribeEvent
        public static void serverStartedEvents(ServerStartedEvent event) {
            Validate.checkLevels(event.getServer());
        }

        @SubscribeEvent
        public static void playerLoggedInEvents(PlayerEvent.PlayerLoggedInEvent event) {
            Network.sendProduce(event.getEntity().level(), event.getEntity());
        }

    }

    @Mod.EventBusSubscriber(modid = "compost", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonModBusEvents {

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            ClientboundProducePacket.CHANNEL.messageBuilder(ClientboundProducePacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(ClientboundProducePacket::write).decoder(ClientboundProducePacket::new).consumerMainThread(ClientboundProducePacket::handle).add();
            ClientboundResetPacket.CHANNEL.messageBuilder(ClientboundResetPacket.class, NetworkDirection.PLAY_TO_CLIENT).encoder(ClientboundResetPacket::write).decoder(ClientboundResetPacket::new).consumerMainThread(ClientboundResetPacket::handle).add();
        }

        @SubscribeEvent
        public static void registerCommandArgumentTypes(RegisterEvent event) {
            event.register(Registries.COMMAND_ARGUMENT_TYPE, ResourceLocation.tryParse("compost:produce"), () -> ArgumentTypeInfos.registerByClass(ProduceArgument.class, SingletonArgumentInfo.contextFree(ProduceArgument::produce)));
        }

    }

    public Compost(FMLJavaModLoadingContext context) {
        Config.loadConfig();

        MinecraftForge.EVENT_BUS.register(this);

        BLOCK_ENTITIES.register(context.getModEventBus());
    }

}