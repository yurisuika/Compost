package dev.yurisuika.compost.network.protocol.common.custom;

import dev.yurisuika.compost.util.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;

public record ResetPayload() implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, ResetPayload> STREAM_CODEC = StreamCodec.unit(new ResetPayload());
    public static final Type<ResetPayload> TYPE = new Type<>(ResourceLocation.tryParse("compost:reset"));

    @Override
    public Type<ResetPayload> type() {
        return TYPE;
    }

    public static void handle(final ResetPayload message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Network.setStacks(new ArrayList<>());
        });
    }

}