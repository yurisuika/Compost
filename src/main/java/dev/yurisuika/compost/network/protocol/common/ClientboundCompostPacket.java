package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.config.options.Composition;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;

public record ClientboundCompostPacket(String name, String item, Double chance, Integer min, Integer max) {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:compost");

    public ClientboundCompostPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(name());
        buffer.writeUtf(item());
        buffer.writeDouble(chance());
        buffer.writeInt(min());
        buffer.writeInt(max());
    }

    public static void handle(Minecraft minecraft, ClientPacketListener listener, FriendlyByteBuf buffer, PacketSender sender) {
        Network.COMPOSITIONS.put(buffer.readUtf(), new Composition(new Composition.Compost(buffer.readUtf(), buffer.readDouble(), new Composition.Compost.Count(buffer.readInt(), buffer.readInt())), new HashSet<>()));
    }

}