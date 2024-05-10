package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.packet.s2c.CompostS2CPacket;
import net.minecraft.util.Identifier;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class CompostHandler {

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new Identifier("compost", "items"))
            .networkProtocolVersion(() -> "1")
            .clientAcceptedVersions(status -> true)
            .serverAcceptedVersions(status -> true)
            .simpleChannel();

    public static void register() {
        CHANNEL.messageBuilder(CompostS2CPacket.class, 1, PlayNetworkDirection.PLAY_TO_CLIENT)
                .encoder(CompostS2CPacket::encode)
                .decoder(CompostS2CPacket::decode)
                .consumerMainThread(CompostS2CPacket::handle)
                .add();
    }

}