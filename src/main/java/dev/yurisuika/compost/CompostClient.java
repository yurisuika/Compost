package dev.yurisuika.compost;

import dev.yurisuika.compost.network.packet.s2c.custom.CompostPayload;
import dev.yurisuika.compost.server.option.CompostConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import java.util.ArrayList;
import java.util.List;

public class CompostClient implements ClientModInitializer {

    public static CompostConfig.Config.World.Group[] GROUPS;

    public static void registerClientEvents() {
        PayloadTypeRegistry.playS2C().register(CompostPayload.ID, CompostPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(CompostPayload.ID, (payload, context) -> {
            List<CompostConfig.Config.World.Group> list = new ArrayList<>();
            for (int i = 0; i < payload.chance().size(); i++) {
                list.add(new CompostConfig.Config.World.Group(payload.item().get(i), payload.chance().get(i), payload.min().get(i), payload.max().get(i)));
            }
            context.client().execute(() -> {
                GROUPS = list.toArray(new CompostConfig.Config.World.Group[0]);
            });
        });
    }

    @Override
    public void onInitializeClient() {
        registerClientEvents();
    }

}