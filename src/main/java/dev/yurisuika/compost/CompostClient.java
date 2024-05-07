package dev.yurisuika.compost;

import dev.yurisuika.compost.server.option.CompostConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CompostClient implements ClientModInitializer {

    public static CompostConfig.Config.World.Group[] GROUPS;

    public static void registerClientEvents() {
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("compost", "group"), (client, handler, buf, responseSender) -> {
            List<CompostConfig.Config.World.Group> list = new ArrayList<>();
            int length = buf.readInt();
            for (int i = 0; i < length; i++) {
                list.add(new CompostConfig.Config.World.Group(buf.readString(), buf.readDouble(), buf.readInt(), buf.readInt()));
            }
            client.execute(() -> {
                GROUPS = list.toArray(new CompostConfig.Config.World.Group[0]);
            });
        });
    }

    @Override
    public void onInitializeClient() {
        registerClientEvents();
    }

}