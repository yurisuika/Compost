package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public final class ClientboundResetPacket {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:reset");
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(ID).networkProtocolVersion(() -> "1").clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true).simpleChannel();

    public ClientboundResetPacket() {}

    public ClientboundResetPacket(FriendlyByteBuf buffer) {
        this();
    }

    public void read(FriendlyByteBuf buffer) {}

    public static void write(ClientboundResetPacket packet, FriendlyByteBuf buffer) {}

    public static void handle(ClientboundResetPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> Network.COMPOSITIONS.clear());
        context.get().setPacketHandled(true);
    }

}