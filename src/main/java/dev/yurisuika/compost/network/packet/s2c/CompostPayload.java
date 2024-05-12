package dev.yurisuika.compost.network.packet.s2c;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record CompostPayload(List<String> names, List<Double> chances, List<Integer> mins, List<Integer> maxes) implements CustomPayload {

    public static final Id<CompostPayload> ID = CustomPayload.id("compost:items");
    public static final PacketCodec<PacketByteBuf, CompostPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING.collect(PacketCodecs.toList()), CompostPayload::names, PacketCodecs.DOUBLE.collect(PacketCodecs.toList()), CompostPayload::chances, PacketCodecs.INTEGER.collect(PacketCodecs.toList()), CompostPayload::mins, PacketCodecs.INTEGER.collect(PacketCodecs.toList()), CompostPayload::maxes, CompostPayload::new);

    @Override
    public Id<CompostPayload> getId() {
        return ID;
    }

}