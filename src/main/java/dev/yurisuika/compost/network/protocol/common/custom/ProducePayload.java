package dev.yurisuika.compost.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ProducePayload(String item, Double chance, Integer min, Integer max) implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, ProducePayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ProducePayload::item, ByteBufCodecs.DOUBLE, ProducePayload::chance, ByteBufCodecs.INT, ProducePayload::min, ByteBufCodecs.INT, ProducePayload::max, ProducePayload::new);
    public static final Type<ProducePayload> TYPE = CustomPacketPayload.createType("compost:produce");

    @Override
    public Type<ProducePayload> type() {
        return TYPE;
    }

}