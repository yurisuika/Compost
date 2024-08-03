package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.protocol.common.ClientboundProducePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class ProduceHandler {

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.tryParse("compost:produce"))
            .networkProtocolVersion(() -> "1")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

    public static void register() {
        CHANNEL.registerMessage(1, ClientboundProducePacket.class, ClientboundProducePacket::encode, ClientboundProducePacket::decode, ClientboundProducePacket::handle);
    }

}