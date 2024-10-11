package dev.yurisuika.compost.network.protocol.common.custom;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import dev.yurisuika.compost.util.config.options.Produce;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ProducePayload(String item, Double chance, Integer min, Integer max) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:produce");
    public static final StreamCodec<FriendlyByteBuf, ProducePayload> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ProducePayload::item, ByteBufCodecs.DOUBLE, ProducePayload::chance, ByteBufCodecs.INT, ProducePayload::min, ByteBufCodecs.INT, ProducePayload::max, ProducePayload::new);
    public static final Type<ProducePayload> TYPE = new Type<>(ID);

    @Override
    public Type<ProducePayload> type() {
        return TYPE;
    }

    public static void handle(ProducePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Produce produce = new Produce(payload.item(), payload.chance(), payload.min(), payload.max());
            Network.getStacks().add(Parse.createItemStack(produce));
            Network.getProduce().add(produce);
        });
    }

}