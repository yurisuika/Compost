package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

public record ClientboundWorldPacket(String name, String world) {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("compost", "world");
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(ID).optional().networkProtocolVersion(1).acceptedVersions((status, version) -> true).simpleChannel();

    public ClientboundWorldPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUtf());
    }

    public static void write(ClientboundWorldPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.name());
        buffer.writeUtf(packet.world());
    }

    public static void handle(ClientboundWorldPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> Network.COMPOSITIONS.get(packet.name()).getWorlds().add(packet.world()));
        context.setPacketHandled(true);
    }

}