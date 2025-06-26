package dev.yurisuika.compost.server.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class ServerJoinEvent {

    public static final Event<Join> JOIN = EventFactory.createArrayBacked(Join.class, (callbacks) -> (listener, netManager, player) -> {
        for (Join callback : callbacks) {
            callback.onJoin(listener, netManager, player);
        }
    });

    public interface Join {

        void onJoin(PlayerList listener, Connection netManager, ServerPlayer player);

    }

}