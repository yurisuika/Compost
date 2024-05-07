package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.packet.s2c.CompostS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class CompostHandler {

    public static SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
        .named(new Identifier("compost", "groups"))
        .networkProtocolVersion(() -> "1")
        .clientAcceptedVersions(s -> true)
        .serverAcceptedVersions(s -> true)
        .simpleChannel();

    public static void register() {
        CHANNEL.registerMessage(1, CompostS2CPacket.class, CompostS2CPacket::encode, CompostS2CPacket::decode, CompostS2CPacket::handle);
    }

}