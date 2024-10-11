package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public record ClientboundResetPacket() implements FabricPacket {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:reset");
    public static final PacketType<ClientboundResetPacket> TYPE = PacketType.create(ID, ClientboundResetPacket::new);

    public ClientboundResetPacket(FriendlyByteBuf buffer) {
        this();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {}

    public static void handle(Minecraft minecraft, ClientPacketListener listener, FriendlyByteBuf buffer, PacketSender sender) {
        Network.setStacks(new ArrayList<>());
        Network.setProduce(new ArrayList<>());
    }

    @Override
    public PacketType<ClientboundResetPacket> getType() {
        return TYPE;
    }

}