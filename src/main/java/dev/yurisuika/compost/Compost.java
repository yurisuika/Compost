package dev.yurisuika.compost;

import dev.yurisuika.compost.block.entity.ComposterBlockEntity;
import dev.yurisuika.compost.network.handler.CompostHandler;
import dev.yurisuika.compost.server.command.CompostCommand;
import dev.yurisuika.compost.util.CompostUtil;
import dev.yurisuika.compost.util.ConfigUtil;
import dev.yurisuika.compost.util.NetworkUtil;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;

@Mod("compost")
public class Compost {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "compost");

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ComposterBlockEntity>> COMPOSTER = BLOCK_ENTITIES.register("composter", () -> BlockEntityType.Builder.create(ComposterBlockEntity::new, Blocks.COMPOSTER).build(null));

    @EventBusSubscriber(modid = "compost")
    public static class CommonForgeEvents {

        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            CompostCommand.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }

        @SubscribeEvent
        public static void serverStartedEvents(ServerStartedEvent event) {
            CompostUtil.checkLevels(Objects.requireNonNull(event.getServer()));
        }

        @SubscribeEvent
        public static void playerLoggedInEvents(PlayerEvent.PlayerLoggedInEvent event) {
            NetworkUtil.sendItems(event.getEntity().getWorld(), event.getEntity());
        }

    }

    @EventBusSubscriber(modid = "compost", bus = EventBusSubscriber.Bus.MOD)
    public static class CommonModBusEvents {

        @SubscribeEvent
        public static void registerPayloadHandlerEvents(RegisterPayloadHandlersEvent event) {
            CompostHandler.register(event);
        }

    }

    public Compost() {
        ConfigUtil.loadConfig();

        BLOCK_ENTITIES.register(ModLoadingContext.get().getActiveContainer().getEventBus());
    }

}