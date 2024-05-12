package dev.yurisuika.compost.network.packet.s2c;

import dev.yurisuika.compost.config.CompostConfig;
import dev.yurisuika.compost.util.CompostUtil;
import dev.yurisuika.compost.util.NetworkUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record CompostPayload(List<String> names, List<Double> chances, List<Integer> mins, List<Integer> maxes) implements CustomPayload {

    public static final Id<CompostPayload> ID = CustomPayload.id("compost:items");
    public static final PacketCodec<PacketByteBuf, CompostPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING.collect(PacketCodecs.toList()), CompostPayload::names, PacketCodecs.DOUBLE.collect(PacketCodecs.toList()), CompostPayload::chances, PacketCodecs.INTEGER.collect(PacketCodecs.toList()), CompostPayload::mins, PacketCodecs.INTEGER.collect(PacketCodecs.toList()), CompostPayload::maxes, CompostPayload::new);

    public static void handle(final CompostPayload message, IPayloadContext context) {
        context.enqueueWork(() -> {
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < message.names.size(); i++) {
                ItemStack stack = CompostUtil.createItemStack(new CompostConfig.Config.Level.Item(message.names.get(i), message.chances.get(i), message.mins.get(i), message.maxes.get(i)));
                stack.setCount(message.maxes.get(i));
                list.add(stack);
            }
            NetworkUtil.stacks = list;
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}