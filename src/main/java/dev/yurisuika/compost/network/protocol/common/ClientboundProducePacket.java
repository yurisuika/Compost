package dev.yurisuika.compost.network.protocol.common;

import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import dev.yurisuika.compost.util.config.options.Produce;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundProducePacket(String item, Double chance, Integer min, Integer max) {

    public static final ResourceLocation ID = ResourceLocation.tryParse("compost:produce");

    public ClientboundProducePacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(item());
        buffer.writeDouble(chance());
        buffer.writeInt(min());
        buffer.writeInt(max());
    }

    public static void handle(Minecraft minecraft, ClientPacketListener listener, FriendlyByteBuf buffer, PacketSender sender) {
        Produce produce = new Produce(buffer.readUtf(), buffer.readDouble(), buffer.readInt(), buffer.readInt());
        Network.getStacks().add(Parse.createItemStack(VanillaRegistries.createLookup(), produce));
        Network.getProduce().add(produce);
    }

}