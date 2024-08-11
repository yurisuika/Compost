package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import dev.yurisuika.compost.util.config.options.Produce;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record ClientboundProducePacket(String item, Double chance, Integer min, Integer max) {

    public static void encode(ClientboundProducePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.item);
        buffer.writeDouble(packet.chance);
        buffer.writeInt(packet.min);
        buffer.writeInt(packet.max);
    }

    public static ClientboundProducePacket decode(FriendlyByteBuf buffer) {
        return new ClientboundProducePacket(buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
    }

    public static void handle(final ClientboundProducePacket message, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            Network.getStacks().add(Parse.createItemStack(context.getSender().getServer().registryAccess(), new Produce(message.item(), message.chance(), message.min(), message.max())));
        });
        context.setPacketHandled(true);
    }

}