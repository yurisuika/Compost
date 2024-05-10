package dev.yurisuika.compost.network.packet.s2c;

import dev.yurisuika.compost.config.CompostConfig;
import dev.yurisuika.compost.util.CompostUtil;
import dev.yurisuika.compost.util.NetworkUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record CompostS2CPacket(List<String> names, List<Double> chances, List<Integer> mins, List<Integer> maxes) implements CustomPayload {

    public static final Identifier ID = new Identifier("compost", "items");

    public CompostS2CPacket(PacketByteBuf buf) {
        this(buf.readList(PacketByteBuf::readString), buf.readList(PacketByteBuf::readDouble), buf.readList(PacketByteBuf::readInt), buf.readList(PacketByteBuf::readInt));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeCollection(names, PacketByteBuf::writeString);
        buf.writeCollection(chances, PacketByteBuf::writeDouble);
        buf.writeCollection(mins, PacketByteBuf::writeInt);
        buf.writeCollection(maxes, PacketByteBuf::writeInt);
    }

    @Override
    public Identifier id() {
        return ID;
    }

    public static void handle(final CompostS2CPacket message, PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < message.names.size(); i++) {
                ItemStack stack = CompostUtil.createItemStack(new CompostConfig.Config.Level.Item(message.names.get(i), message.chances.get(i), message.mins.get(i), message.maxes.get(i)));
                stack.setCount(message.maxes.get(i));
                list.add(stack);
            }
            NetworkUtil.stacks = list;
        });
    }

}