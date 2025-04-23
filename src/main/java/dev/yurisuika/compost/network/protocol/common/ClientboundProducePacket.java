package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import dev.yurisuika.compost.util.config.options.Produce;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

public record ClientboundProducePacket(String item, Double chance, Integer min, Integer max) {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:produce");
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(ID).optional().networkProtocolVersion(1).acceptedVersions((status, version) -> true).simpleChannel();

    public ClientboundProducePacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
    }

    public static void write(ClientboundProducePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.item());
        buffer.writeDouble(packet.chance());
        buffer.writeInt(packet.min());
        buffer.writeInt(packet.max());
    }

    public static void handle(ClientboundProducePacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            Produce produce = new Produce(packet.item(), packet.chance(), packet.min(), packet.max());
            Network.getStacks().add(Parse.createItemStack(context.getSender().registryAccess(), produce));
            Network.getProduce().add(produce);
        });
        context.setPacketHandled(true);
    }

}