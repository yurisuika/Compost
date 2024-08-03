package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.protocol.common.ClientboundProducePacket;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class ProduceHandler {

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.tryParse("compost:produce"))
            .networkProtocolVersion(() -> "1")
            .clientAcceptedVersions(status -> true)
            .serverAcceptedVersions(status -> true)
            .simpleChannel();

    public static void register() {
        CHANNEL.messageBuilder(ClientboundProducePacket.class, 1, PlayNetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundProducePacket::encode)
                .decoder(ClientboundProducePacket::decode)
                .consumerMainThread(ClientboundProducePacket::handle)
                .add();
    }

}