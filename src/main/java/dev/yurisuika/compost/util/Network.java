package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.protocol.common.ClientboundCompostPacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundWorldPacket;
import dev.yurisuika.compost.world.Composition;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class Network {

    public static String levelName;
    public final static Map<String, Composition> COMPOSITIONS = new HashMap<>();

    public static String getLevelName() {
        return levelName;
    }

    public static void setLevelName(String levelName) {
        Network.levelName = levelName;
    }

    public static void sendCompositions(Level level, ServerPlayer player) {
        if (!level.isClientSide()) {
            FriendlyByteBuf resetBuffer = new FriendlyByteBuf(Unpooled.EMPTY_BUFFER);
            player.connection.send(new ClientboundCustomPayloadPacket(ClientboundResetPacket.ID, resetBuffer));
            COMPOSITIONS.clear();
            Configure.getCompositions().forEach((name, composition) -> {
                FriendlyByteBuf compostBuffer = new FriendlyByteBuf(Unpooled.buffer());
                compostBuffer.writeUtf(name);
                compostBuffer.writeUtf(composition.getCompost().getItem());
                compostBuffer.writeDouble(composition.getCompost().getChance());
                compostBuffer.writeInt(composition.getCompost().getCount().getMin());
                compostBuffer.writeInt(composition.getCompost().getCount().getMax());
                player.connection.send(new ClientboundCustomPayloadPacket(ClientboundCompostPacket.ID, compostBuffer));
                composition.getWorlds().forEach(world -> {
                    FriendlyByteBuf worldBuffer = new FriendlyByteBuf(Unpooled.buffer());
                    worldBuffer.writeUtf(name);
                    worldBuffer.writeUtf(world);
                    player.connection.send(new ClientboundCustomPayloadPacket(ClientboundWorldPacket.ID, worldBuffer));
                });
                COMPOSITIONS.put(name, composition);
            });
        }
    }

}