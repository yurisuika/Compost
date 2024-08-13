package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.function.Supplier;

public record ClientboundResetPacket() {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:reset");
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(ID).networkProtocolVersion(() -> "1").clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true).simpleChannel();

    public ClientboundResetPacket(FriendlyByteBuf buffer) {
        this();
    }

    public static void write(ClientboundResetPacket packet, FriendlyByteBuf buffer) {}


    public static void handle(ClientboundResetPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> Network.setStacks(new ArrayList<>()));
        context.get().setPacketHandled(true);
    }

}