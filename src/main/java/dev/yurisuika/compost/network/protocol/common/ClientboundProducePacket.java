package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import dev.yurisuika.compost.util.config.options.Produce;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public record ClientboundProducePacket(String item, Double chance, Integer min, Integer max) {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:produce");
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(ID).networkProtocolVersion(() -> "1").clientAcceptedVersions(status -> true).serverAcceptedVersions(status -> true).simpleChannel();

    public ClientboundProducePacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
    }

    public static void write(ClientboundProducePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.item());
        buffer.writeDouble(packet.chance());
        buffer.writeInt(packet.min());
        buffer.writeInt(packet.max());
    }

    public static void handle(ClientboundProducePacket packet, NetworkEvent.ClientCustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            Produce produce = new Produce(packet.item(), packet.chance(), packet.min(), packet.max());
            Network.getStacks().add(Parse.createItemStack(produce));
            Network.getProduce().add(produce);
        });
        context.setPacketHandled(true);
    }

}