package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public final class ClientboundWorldPacket {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:world");
    public String name;
    public String world;

    public ClientboundWorldPacket(String name, String world) {
        this.name = name;
        this.world = world;
    }

    public ClientboundWorldPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUtf());
    }

    public void read(FriendlyByteBuf buffer) {
        this.name = buffer.readUtf();
        this.world = buffer.readUtf();
    }

    public static void write(ClientboundWorldPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.name());
        buffer.writeUtf(packet.world());
    }

    public static void handle(Minecraft minecraft, ClientPacketListener listener, FriendlyByteBuf buffer, PacketSender sender) {
        Network.COMPOSITIONS.get(buffer.readUtf()).getWorlds().add(buffer.readUtf());
    }

    public String name() {
        return name;
    }

    public String world() {
        return world;
    }

}