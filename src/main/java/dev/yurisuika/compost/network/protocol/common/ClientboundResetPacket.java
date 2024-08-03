package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public final class ClientboundResetPacket {

    public ClientboundResetPacket() {}

    public static void encode(ClientboundResetPacket packet, FriendlyByteBuf buffer) {}

    public static ClientboundResetPacket decode(FriendlyByteBuf buffer) {
        return new ClientboundResetPacket();
    }

    public static void handle(final ClientboundResetPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Network.setStacks(new ArrayList<>());
        });
        context.get().setPacketHandled(true);
    }

}