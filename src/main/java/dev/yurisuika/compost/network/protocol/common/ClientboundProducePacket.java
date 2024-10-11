package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import dev.yurisuika.compost.util.config.options.Produce;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public final class ClientboundProducePacket {

    public String item;
    public Double chance;
    public Integer min;
    public Integer max;
    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:produce");
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(ID).networkProtocolVersion(() -> "1").clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true).simpleChannel();

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

    public static void handle(ClientboundProducePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Produce produce = new Produce(packet.item(), packet.chance(), packet.min(), packet.max());
            Network.getStacks().add(Parse.createItemStack(produce));
            Network.getProduce().add(produce);
        });
        context.get().setPacketHandled(true);
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