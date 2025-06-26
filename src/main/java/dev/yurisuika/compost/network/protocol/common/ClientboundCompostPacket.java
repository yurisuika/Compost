package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.world.Composition;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;

public final class ClientboundCompostPacket {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:compost");
    public String name;
    public String item;
    public Double chance;
    public Integer min;
    public Integer max;

    public ClientboundCompostPacket(String name, String item, Double chance, Integer min, Integer max) {
        this.name = name;
        this.item = item;
        this.chance = chance;
        this.min = min;
        this.max = max;
    }

    public ClientboundCompostPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
    }

    public void read(FriendlyByteBuf buffer) {
        this.name = buffer.readUtf();
        this.item = buffer.readUtf();
        this.chance = buffer.readDouble();
        this.min = buffer.readInt();
        this.max = buffer.readInt();
    }

    public static void write(ClientboundCompostPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.name());
        buffer.writeUtf(packet.item());
        buffer.writeDouble(packet.chance());
        buffer.writeInt(packet.min());
        buffer.writeInt(packet.max());
    }

    public static void handle(PacketContext context, FriendlyByteBuf buffer) {
        Network.COMPOSITIONS.put(buffer.readUtf(), new Composition(new Composition.Compost(buffer.readUtf(), buffer.readDouble(), new Composition.Compost.Count(buffer.readInt(), buffer.readInt())), new HashSet<>()));
    }

    public String name() {
        return name;
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