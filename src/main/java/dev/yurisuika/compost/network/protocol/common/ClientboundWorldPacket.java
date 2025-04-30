package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public final class ClientboundWorldPacket {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:world");
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(ID).networkProtocolVersion(() -> "1").clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true).simpleChannel();
    public String name;
    public String world;

    public ClientboundWorldPacket(String name, String world) {
        this.name = name;
        this.world = world;
    }

    public ClientboundWorldPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUtf());
    }

    public void read(FriendlyByteBuf buffer) {
        this.name = buffer.readUtf();
        this.world = buffer.readUtf();
    }

    public static void write(ClientboundWorldPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.name());
        buffer.writeUtf(packet.world());
    }

    public static void handle(ClientboundWorldPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> Network.getNetworkCompositions().get(packet.name()).getWorlds().add(packet.world()));
        context.get().setPacketHandled(true);
    }

    public String name() {
        return name;
    }

    public String world() {
        return world;
    }

}