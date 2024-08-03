package dev.yurisuika.compost.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ResetPayload() implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, ResetPayload> STREAM_CODEC = StreamCodec.unit(new ResetPayload());
    public static final Type<ResetPayload> TYPE = CustomPacketPayload.createType("compost:reset");

    @Override
    public Type<ResetPayload> type() {
        return TYPE;
    }

}