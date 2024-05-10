package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.packet.s2c.CompostS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

public class CompostHandler {

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(new Identifier("compost", "items"))
            .optional()
            .networkProtocolVersion(1)
            .acceptedVersions((status, version) -> true)
            .simpleChannel();

    public static void register() {
        CHANNEL.messageBuilder(CompostS2CPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CompostS2CPacket::encode)
                .decoder(CompostS2CPacket::decode)
                .consumerMainThread(CompostS2CPacket::handle)
                .add();
    }

}