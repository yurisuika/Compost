package dev.yurisuika.compost.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record CompostPayload(List<String> item, List<Double> chance, List<Integer> min, List<Integer> max) implements CustomPayload {

    public static final Id<CompostPayload> ID = CustomPayload.id("compost:groups");
    public static final PacketCodec<PacketByteBuf, CompostPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING.collect(PacketCodecs.toList()), CompostPayload::item, PacketCodecs.DOUBLE.collect(PacketCodecs.toList()), CompostPayload::chance, PacketCodecs.INTEGER.collect(PacketCodecs.toList()), CompostPayload::min, PacketCodecs.INTEGER.collect(PacketCodecs.toList()), CompostPayload::max, CompostPayload::new);

    @Override
    public Id<CompostPayload> getId() {
        return ID;
    }

}