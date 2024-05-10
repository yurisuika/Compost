package dev.yurisuika.compost.network.packet.s2c;

import dev.yurisuika.compost.config.CompostConfig;
import dev.yurisuika.compost.util.CompostUtil;
import dev.yurisuika.compost.util.NetworkUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;
import java.util.List;

public class CompostS2CPacket {

    public List<String> names;
    public List<Double> chances;
    public List<Integer> mins;
    public List<Integer> maxes;

    public CompostS2CPacket(List<String> names, List<Double> chances, List<Integer> mins, List<Integer> maxes) {
        this.names = names;
        this.chances = chances;
        this.mins = mins;
        this.maxes = maxes;
    }

    public static void encode(CompostS2CPacket packet, PacketByteBuf buf) {
        buf.writeCollection(packet.names, PacketByteBuf::writeString);
        buf.writeCollection(packet.chances, PacketByteBuf::writeDouble);
        buf.writeCollection(packet.mins, PacketByteBuf::writeInt);
        buf.writeCollection(packet.maxes, PacketByteBuf::writeInt);
    }

    public static CompostS2CPacket decode(PacketByteBuf buf) {
        return new CompostS2CPacket(buf.readList(PacketByteBuf::readString), buf.readList(PacketByteBuf::readDouble), buf.readList(PacketByteBuf::readInt), buf.readList(PacketByteBuf::readInt));
    }

    public static void handle(final CompostS2CPacket message, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < message.names.size(); i++) {
                ItemStack stack = CompostUtil.createItemStack(new CompostConfig.Config.Level.Item(message.names.get(i), message.chances.get(i), message.mins.get(i), message.maxes.get(i)));
                stack.setCount(message.maxes.get(i));
                list.add(stack);
            }
            NetworkUtil.stacks = list;
        });
        context.setPacketHandled(true);
    }

}