package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundWorldPacket(String name, String world) {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:world");

    public ClientboundWorldPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUtf());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(name());
        buffer.writeUtf(world());
    }

    public static void handle(Minecraft minecraft, ClientPacketListener listener, FriendlyByteBuf buffer, PacketSender sender) {
        Network.COMPOSITIONS.get(buffer.readUtf()).getWorlds().add(buffer.readUtf());
    }

}