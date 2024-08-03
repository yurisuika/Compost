package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ResetHandler {

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.tryParse("compost:reset"))
            .networkProtocolVersion(() -> "1")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

    public static void register() {
        CHANNEL.registerMessage(1, ClientboundResetPacket.class, ClientboundResetPacket::encode, ClientboundResetPacket::decode, ClientboundResetPacket::handle);
    }

}