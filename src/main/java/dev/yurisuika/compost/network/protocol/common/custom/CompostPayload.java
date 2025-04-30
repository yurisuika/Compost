package dev.yurisuika.compost.network.protocol.common.custom;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.config.options.Composition;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;

public record CompostPayload(String name, String item, Double chance, Integer min, Integer max) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("compost", "compost");
    public static final StreamCodec<FriendlyByteBuf, CompostPayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, CompostPayload::name, ByteBufCodecs.STRING_UTF8, CompostPayload::item, ByteBufCodecs.DOUBLE, CompostPayload::chance, ByteBufCodecs.INT, CompostPayload::min, ByteBufCodecs.INT, CompostPayload::max, CompostPayload::new);
    public static final Type<CompostPayload> TYPE = new Type<>(ID);

    @Override
    public Type<CompostPayload> type() {
        return TYPE;
    }

    public static void handle(CompostPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> Network.getNetworkCompositions().put(payload.name(), new Composition(new Composition.Compost(payload.item(), payload.chance(), new Composition.Compost.Count(payload.min(), payload.max())), new HashSet<>())));
    }

}