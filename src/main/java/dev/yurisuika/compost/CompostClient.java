package dev.yurisuika.compost;

import dev.yurisuika.compost.network.packet.s2c.custom.CompostPayload;
import dev.yurisuika.compost.config.CompostConfig;
import dev.yurisuika.compost.util.CompostUtil;
import dev.yurisuika.compost.util.NetworkUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CompostClient implements ClientModInitializer {

    public static void registerClientEvents() {
        PayloadTypeRegistry.playS2C().register(CompostPayload.ID, CompostPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(CompostPayload.ID, (payload, context) -> {
            List<ItemStack> stacks = new ArrayList<>();
            for (int i = 0; i < payload.names().size(); i++) {
                stacks.add(CompostUtil.createItemStack(new CompostConfig.Config.Level.Item(payload.names().get(i), payload.chances().get(i), payload.mins().get(i), payload.maxes().get(i))));
            }
            context.client().execute(() -> {
                NetworkUtil.stacks = stacks;
            });
        });
    }

    @Override
    public void onInitializeClient() {
        registerClientEvents();
    }

}