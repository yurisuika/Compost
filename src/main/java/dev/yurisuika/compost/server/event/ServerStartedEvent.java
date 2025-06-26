package dev.yurisuika.compost.server.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;

public final class ServerStartedEvent {

    public static final Event<ServerStarted> SERVER_STARTED = EventFactory.createArrayBacked(ServerStarted.class, (callbacks) -> (server) -> {
        for (ServerStarted callback : callbacks) {
            callback.onServerStarted(server);
        }
    });

    public interface ServerStarted {

        void onServerStarted(MinecraftServer server);

    }

}