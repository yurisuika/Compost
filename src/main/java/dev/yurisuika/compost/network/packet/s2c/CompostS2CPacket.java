package dev.yurisuika.compost.network.packet.s2c;

import dev.yurisuika.compost.server.option.CompostConfig;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static dev.yurisuika.compost.Compost.*;

public class CompostS2CPacket {

    public List<String> item;
    public List<Double> chance;
    public List<Integer> min;
    public List<Integer> max;

    public CompostS2CPacket(List<String> item, List<Double> chance, List<Integer> min, List<Integer> max) {
        this.item = item;
        this.chance = chance;
        this.min = min;
        this.max = max;
    }

    public static void encode(CompostS2CPacket packet, PacketByteBuf buf) {
        buf.writeCollection(packet.item, PacketByteBuf::writeString);
        buf.writeCollection(packet.chance, PacketByteBuf::writeDouble);
        buf.writeCollection(packet.min, PacketByteBuf::writeInt);
        buf.writeCollection(packet.max, PacketByteBuf::writeInt);
    }

    public static CompostS2CPacket decode(PacketByteBuf buf) {
        return new CompostS2CPacket(buf.readList(PacketByteBuf::readString), buf.readList(PacketByteBuf::readDouble), buf.readList(PacketByteBuf::readInt), buf.readList(PacketByteBuf::readInt));
    }

    public static void handle(final CompostS2CPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            List<CompostConfig.Config.World.Group> list = new ArrayList<>();
            for (int i = 0; i < message.item.size(); i++) {
                list.add(new CompostConfig.Config.World.Group(message.item.get(i), message.chance.get(i), message.min.get(i), message.max.get(i)));
            }
            GROUPS = list.toArray(new CompostConfig.Config.World.Group[0]);
        });
        context.get().setPacketHandled(true);
    }

}