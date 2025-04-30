package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public record ClientboundWorldPacket(String name, String world) {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:world");
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(ID).networkProtocolVersion(() -> "1").clientAcceptedVersions(status -> true).serverAcceptedVersions(status -> true).simpleChannel();

    public ClientboundWorldPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUtf());
    }

    public static void write(ClientboundWorldPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.name());
        buffer.writeUtf(packet.world());
    }

    public static void handle(ClientboundWorldPacket packet, NetworkEvent.ClientCustomPayloadEvent.Context context) {
        context.enqueueWork(() -> Network.getNetworkCompositions().get(packet.name()).getWorlds().add(packet.world()));
        context.setPacketHandled(true);
    }

}