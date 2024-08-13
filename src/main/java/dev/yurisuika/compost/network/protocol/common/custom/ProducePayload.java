package dev.yurisuika.compost.network.protocol.common.custom;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import dev.yurisuika.compost.util.config.options.Produce;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ProducePayload(String item, Double chance, Integer min, Integer max) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:produce");

    public ProducePayload(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(item());
        buffer.writeDouble(chance());
        buffer.writeInt(min());
        buffer.writeInt(max());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static void handle(ProducePayload payload, IPayloadContext context) {
        context.workHandler().submitAsync(() -> Network.getStacks().add(Parse.createItemStack(new Produce(payload.item(), payload.chance(), payload.min(), payload.max()))));
    }

}