package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.protocol.common.ClientboundProducePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

public class ProduceHandler {

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(ResourceLocation.tryParse("compost:produce"))
            .optional()
            .networkProtocolVersion(1)
            .acceptedVersions((status, version) -> true)
            .simpleChannel();

    public static void register() {
        CHANNEL.messageBuilder(ClientboundProducePacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundProducePacket::encode)
                .decoder(ClientboundProducePacket::decode)
                .consumerMainThread(ClientboundProducePacket::handle)
                .add();
    }

}