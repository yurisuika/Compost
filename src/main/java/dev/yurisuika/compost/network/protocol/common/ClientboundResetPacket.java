package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
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

    public static void handle(Minecraft minecraft, ClientPacketListener listener, FriendlyByteBuf buffer, PacketSender sender) {
        Network.getNetworkCompositions().clear();
    }

}