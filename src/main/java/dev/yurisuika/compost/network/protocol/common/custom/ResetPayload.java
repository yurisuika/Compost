package dev.yurisuika.compost.network.protocol.common.custom;

import dev.yurisuika.compost.util.Network;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public record ResetPayload() implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:reset");
    public static final StreamCodec<FriendlyByteBuf, ResetPayload> STREAM_CODEC = StreamCodec.unit(new ResetPayload());
    public static final Type<ResetPayload> TYPE = new Type<>(ID);

    @Override
    public Type<ResetPayload> type() {
        return TYPE;
    }

    public static void handle(ResetPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            Network.setStacks(new ArrayList<>());
            Network.setProduce(new ArrayList<>());
        });
    }

}