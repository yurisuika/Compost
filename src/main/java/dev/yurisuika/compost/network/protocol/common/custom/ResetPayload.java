package dev.yurisuika.compost.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ResetPayload() implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, ResetPayload> STREAM_CODEC = StreamCodec.unit(new ResetPayload());
    public static final Type<ResetPayload> TYPE = new Type<>(ResourceLocation.tryParse("compost:reset"));

    @Override
    public Type<ResetPayload> type() {
        return TYPE;
    }

}