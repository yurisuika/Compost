package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

public class ResetHandler {

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(ResourceLocation.tryParse("compost:reset"))
            .optional()
            .networkProtocolVersion(1)
            .acceptedVersions((status, version) -> true)
            .simpleChannel();

    public static void register() {
        CHANNEL.messageBuilder(ClientboundResetPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundResetPacket::encode)
                .decoder(ClientboundResetPacket::decode)
                .consumerMainThread(ClientboundResetPacket::handle)
                .add();
    }

}