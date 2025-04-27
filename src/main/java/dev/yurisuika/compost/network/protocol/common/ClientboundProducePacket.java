package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.config.options.Produce;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public final class ClientboundProducePacket {

    public String item;
    public Double chance;
    public Integer min;
    public Integer max;
    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:produce");

    public ClientboundProducePacket(String item, Double chance, Integer min, Integer max) {
        this.item = item;
        this.chance = chance;
        this.min = min;
        this.max = max;
    }

    public ClientboundProducePacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
    }

    public void read(FriendlyByteBuf buffer) {
        this.item = buffer.readUtf();
        this.chance = buffer.readDouble();
        this.min = buffer.readInt();
        this.max = buffer.readInt();
    }

    public static void write(ClientboundProducePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.item());
        buffer.writeDouble(packet.chance());
        buffer.writeInt(packet.min());
        buffer.writeInt(packet.max());
    }

    public static void handle(Minecraft minecraft, ClientPacketListener listener, FriendlyByteBuf buffer, PacketSender sender) {
        Network.getProduce().add(new Produce(buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt()));
    }

    public String item() {
        return item;
    }

    public Double chance() {
        return chance;
    }

    public Integer min() {
        return min;
    }

    public Integer max() {
        return max;
    }

}