package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public record ClientboundResetPacket() {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:reset");
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(ID).networkProtocolVersion(() -> "1").clientAcceptedVersions(status -> true).serverAcceptedVersions(status -> true).simpleChannel();

    public ClientboundResetPacket(FriendlyByteBuf buffer) {
        this();
    }

    public static void write(ClientboundResetPacket packet, FriendlyByteBuf buffer) {}

    public static void handle(ClientboundResetPacket packet, NetworkEvent.ClientCustomPayloadEvent.Context context) {
        context.enqueueWork(() -> Network.COMPOSITIONS.clear());
        context.setPacketHandled(true);
    }

}