package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public final class ClientboundResetPacket {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:reset");

    public ClientboundResetPacket() {}

    public ClientboundResetPacket(FriendlyByteBuf buffer) {
        this();
    }

    public void read(FriendlyByteBuf buffer) {}

    public static void write(ClientboundResetPacket packet, FriendlyByteBuf buffer) {}

    public static void handle(PacketContext context, FriendlyByteBuf buffer) {
        Network.COMPOSITIONS.clear();
    }

}