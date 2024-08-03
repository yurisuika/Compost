package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class ResetHandler {

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.tryParse("compost:reset"))
            .networkProtocolVersion(() -> "1")
            .clientAcceptedVersions(status -> true)
            .serverAcceptedVersions(status -> true)
            .simpleChannel();

    public static void register() {
        CHANNEL.messageBuilder(ClientboundResetPacket.class, 1, PlayNetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundResetPacket::encode)
                .decoder(ClientboundResetPacket::decode)
                .consumerMainThread(ClientboundResetPacket::handle)
                .add();
    }

}