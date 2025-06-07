package dev.yurisuika.compost.network.protocol.common.custom;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.config.options.Composition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;

public record CompostPayload(String name, String item, Double chance, Integer min, Integer max) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:compost");

    public CompostPayload(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(name());
        buffer.writeUtf(item());
        buffer.writeDouble(chance());
        buffer.writeInt(min());
        buffer.writeInt(max());
    }

    public ResourceLocation id() {
        return ID;
    }

    public static void handle(CompostPayload payload, IPayloadContext context) {
        context.workHandler().submitAsync(() -> Network.COMPOSITIONS.put(payload.name(), new Composition(new Composition.Compost(payload.item(), payload.chance(), new Composition.Compost.Count(payload.min(), payload.max())), new HashSet<>())));
    }

}