package dev.yurisuika.compost.network.protocol.common.custom;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import dev.yurisuika.compost.util.config.options.Produce;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ProducePayload(String item, Double chance, Integer min, Integer max) implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, ProducePayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ProducePayload::item, ByteBufCodecs.DOUBLE, ProducePayload::chance, ByteBufCodecs.INT, ProducePayload::min, ByteBufCodecs.INT, ProducePayload::max, ProducePayload::new);
    public static final Type<ProducePayload> TYPE = CustomPacketPayload.createType("compost:produce");

    @Override
    public Type<ProducePayload> type() {
        return TYPE;
    }

    public static void handle(final ProducePayload message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Network.getStacks().add(Parse.createItemStack(new Produce(message.item(), message.chance(), message.min(), message.max())));
        });
    }

}