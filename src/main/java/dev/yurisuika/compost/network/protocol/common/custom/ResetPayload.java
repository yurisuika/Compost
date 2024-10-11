package dev.yurisuika.compost.network.protocol.common.custom;

import dev.yurisuika.compost.util.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;

public record ResetPayload() implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:reset");

    public ResetPayload(FriendlyByteBuf buffer) {
        this();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {}

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static void handle(ResetPayload payload, IPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            Network.setStacks(new ArrayList<>());
            Network.setProduce(new ArrayList<>());
        });
    }

}