package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import dev.yurisuika.compost.util.config.options.Produce;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class ClientboundProducePacket {

    public final String item;
    public final Double chance;
    public final Integer min;
    public final Integer max;

    public ClientboundProducePacket(String item, Double chance, Integer min, Integer max) {
        this.item = item;
        this.chance = chance;
        this.min = min;
        this.max = max;
    }

    public static void encode(ClientboundProducePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.item);
        buffer.writeDouble(packet.chance);
        buffer.writeInt(packet.min);
        buffer.writeInt(packet.max);
    }

    public static ClientboundProducePacket decode(FriendlyByteBuf buffer) {
        return new ClientboundProducePacket(buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
    }

    public static void handle(final ClientboundProducePacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Network.getStacks().add(Parse.createItemStack(new Produce(message.item(), message.chance(), message.min(), message.max())));
        });
        context.get().setPacketHandled(true);
    }

    public String item() {
        return item;
    }

    public Double chance() {
        return chance;
    }

    public Integer min() {
        return min;
    }

    public Integer max() {
        return max;
    }

}