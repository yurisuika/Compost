package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.config.options.Composition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

import java.util.HashSet;

public record ClientboundCompostPacket(String name, String item, Double chance, Integer min, Integer max) {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:compost");
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(ID).networkProtocolVersion(() -> "1").clientAcceptedVersions(status -> true).serverAcceptedVersions(status -> true).simpleChannel();

    public ClientboundCompostPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
    }

    public static void write(ClientboundCompostPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.name());
        buffer.writeUtf(packet.item());
        buffer.writeDouble(packet.chance());
        buffer.writeInt(packet.min());
        buffer.writeInt(packet.max());
    }

    public static void handle(ClientboundCompostPacket packet, NetworkEvent.ClientCustomPayloadEvent.Context context) {
        context.enqueueWork(() -> Network.COMPOSITIONS.put(packet.name(), new Composition(new Composition.Compost(packet.item(), packet.chance(), new Composition.Compost.Count(packet.min(), packet.max())), new HashSet<>())));
        context.setPacketHandled(true);
    }

}