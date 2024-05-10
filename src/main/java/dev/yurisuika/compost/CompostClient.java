package dev.yurisuika.compost;

import dev.yurisuika.compost.util.NetworkUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CompostClient implements ClientModInitializer {

    public static void registerClientEvents() {
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("compost", "items"), (client, handler, buf, responseSender) -> {
            List<ItemStack> list = new ArrayList<>();
            int length = buf.readInt();
            for (int i = 0; i < length; i++) {
                list.add(buf.readItemStack());
            }
            client.execute(() -> {
                NetworkUtil.stacks = list;
            });
        });
    }

    @Override
    public void onInitializeClient() {
        registerClientEvents();
    }

}