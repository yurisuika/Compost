package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.ArrayList;

public record ClientboundResetPacket() {

    public static void encode(ClientboundResetPacket packet, FriendlyByteBuf buffer) {}

    public static ClientboundResetPacket decode(FriendlyByteBuf buffer) {
        return new ClientboundResetPacket();
    }

    public static void handle(final ClientboundResetPacket message, NetworkEvent.ClientCustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            Network.setStacks(new ArrayList<>());
        });
        context.setPacketHandled(true);
    }

}