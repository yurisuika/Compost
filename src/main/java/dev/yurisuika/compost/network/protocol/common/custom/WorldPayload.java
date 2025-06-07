package dev.yurisuika.compost.network.protocol.common.custom;

import dev.yurisuika.compost.util.Network;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record WorldPayload(String name, String world) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("compost", "world");
    public static final StreamCodec<FriendlyByteBuf, WorldPayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, WorldPayload::name, ByteBufCodecs.STRING_UTF8, WorldPayload::world, WorldPayload::new);
    public static final Type<WorldPayload> TYPE = new Type<>(ID);

    @Override
    public Type<WorldPayload> type() {
        return TYPE;
    }

    public static void handle(WorldPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> Network.COMPOSITIONS.get(payload.name()).getWorlds().add(payload.world()));
    }

}