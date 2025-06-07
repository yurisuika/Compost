package dev.yurisuika.compost.network.protocol.common.custom;

import dev.yurisuika.compost.util.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WorldPayload(String name, String world) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:world");

    public WorldPayload(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(name());
        buffer.writeUtf(world());
    }

    public ResourceLocation id() {
        return ID;
    }

    public static void handle(WorldPayload payload, IPayloadContext context) {
        context.workHandler().submitAsync(() -> Network.COMPOSITIONS.get(payload.name()).getWorlds().add(payload.world()));
    }

}