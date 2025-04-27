package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.config.options.Produce;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public record ClientboundProducePacket(String item, Double chance, Integer min, Integer max) {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:produce");
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(ID).networkProtocolVersion(() -> "1").clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true).simpleChannel();

    public ClientboundProducePacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
    }

    public static void write(ClientboundProducePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.item());
        buffer.writeDouble(packet.chance());
        buffer.writeInt(packet.min());
        buffer.writeInt(packet.max());
    }

    public static void handle(ClientboundProducePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> Network.getProduce().add(new Produce(packet.item(), packet.chance(), packet.min(), packet.max())));
        context.get().setPacketHandled(true);
    }

}