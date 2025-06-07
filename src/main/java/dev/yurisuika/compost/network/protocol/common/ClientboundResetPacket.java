package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

public record ClientboundResetPacket() {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("compost", "reset");
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(ID).optional().networkProtocolVersion(1).acceptedVersions((status, version) -> true).simpleChannel();

    public ClientboundResetPacket(FriendlyByteBuf buffer) {
        this();
    }

    public static void write(ClientboundResetPacket packet, FriendlyByteBuf buffer) {}

    public static void handle(ClientboundResetPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> Network.COMPOSITIONS.clear());
        context.setPacketHandled(true);
    }

}