package dev.yurisuika.compost;

import dev.yurisuika.compost.block.entity.ComposterBlockEntity;
import dev.yurisuika.compost.network.handler.CompostHandler;
import dev.yurisuika.compost.server.command.CompostCommand;
import dev.yurisuika.compost.server.option.CompostConfig;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.fmlserverevents.FMLServerStartedEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

import static dev.yurisuika.compost.server.option.CompostConfig.*;
import static dev.yurisuika.compost.util.ConfigUtil.*;
import static dev.yurisuika.compost.util.NetworkUtil.*;

@Mod("compost")
public class Compost {

    public static CompostConfig.Config.World.Group[] GROUPS;

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, "compost");

    public static final RegistryObject<BlockEntityType<ComposterBlockEntity>> COMPOSTER = BLOCK_ENTITIES.register("composter", () -> BlockEntityType.Builder.create(ComposterBlockEntity::new, Blocks.COMPOSTER).build(null));

    @Mod.EventBusSubscriber(modid = "compost")
    public static class CommonForgeEvents {

        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            CompostCommand.register(event.getDispatcher(), event.getEnvironment());
        }

        @SubscribeEvent
        public static void serverStartedEvents(FMLServerStartedEvent event) {
            checkWorlds(Objects.requireNonNull(event.getServer()));
        }

        @SubscribeEvent
        public static void playerLoggedInEvents(PlayerEvent.PlayerLoggedInEvent event) {
            sendGroups(event.getPlayer().getEntityWorld(), event.getPlayer());
        }

    }

    @Mod.EventBusSubscriber(modid = "compost", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonModBusEvents {

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            CompostHandler.register();
        }

    }

    public Compost() {
        if (!file.exists()) {
            saveConfig();
        }
        loadConfig();

        MinecraftForge.EVENT_BUS.register(this);

        BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}