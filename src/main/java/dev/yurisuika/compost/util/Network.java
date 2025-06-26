package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.protocol.common.ClientboundCompostPacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundWorldPacket;
import dev.yurisuika.compost.world.Composition;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
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
            ServerPlayNetworking.send(player, ClientboundResetPacket.ID, PacketByteBufs.empty());
            COMPOSITIONS.clear();
            Configure.getCompositions().forEach((name, composition) -> {
                FriendlyByteBuf compostBuffer = PacketByteBufs.create();
                compostBuffer.writeUtf(name);
                compostBuffer.writeUtf(composition.getCompost().getItem());
                compostBuffer.writeDouble(composition.getCompost().getChance());
                compostBuffer.writeInt(composition.getCompost().getCount().getMin());
                compostBuffer.writeInt(composition.getCompost().getCount().getMax());
                ServerPlayNetworking.send(player, ClientboundCompostPacket.ID, compostBuffer);
                composition.getWorlds().forEach(world -> {
                    FriendlyByteBuf worldBuffer = PacketByteBufs.create();
                    worldBuffer.writeUtf(name);
                    worldBuffer.writeUtf(composition.getCompost().getItem());
                    ServerPlayNetworking.send(player, ClientboundWorldPacket.ID, worldBuffer);
                });
                COMPOSITIONS.put(name, composition);
            });
        }
    }

}