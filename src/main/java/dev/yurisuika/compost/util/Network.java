package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.protocol.common.ClientboundCompostPacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundWorldPacket;
import dev.yurisuika.compost.util.config.Option;
import dev.yurisuika.compost.util.config.options.Composition;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class Network {

    public static String levelName;
    public static Map<String, Composition> networkCompositions = new HashMap<>();

    public static String getLevelName() {
        return levelName;
    }

    public static void setLevelName(String levelName) {
        Network.levelName = levelName;
    }

    public static Map<String, Composition> getNetworkCompositions() {
        return networkCompositions;
    }

    public static void setNetworkCompositions(Map<String, Composition> networkCompositions) {
        Network.networkCompositions = networkCompositions;
    }

    public static void sendCompositions(Level level, Player player) {
        if (!level.isClientSide()) {
            ServerPlayNetworking.send((ServerPlayer) player, ClientboundResetPacket.ID, PacketByteBufs.empty());
            getNetworkCompositions().clear();
            Option.getCompositions().forEach((name, composition) -> {
                FriendlyByteBuf compostBuffer = PacketByteBufs.create();
                compostBuffer.writeUtf(name);
                compostBuffer.writeUtf(composition.getCompost().getItem());
                compostBuffer.writeDouble(composition.getCompost().getChance());
                compostBuffer.writeInt(composition.getCompost().getCount().getMin());
                compostBuffer.writeInt(composition.getCompost().getCount().getMax());
                ServerPlayNetworking.send((ServerPlayer) player, ClientboundCompostPacket.ID, compostBuffer);
                composition.getWorlds().forEach(world -> {
                    FriendlyByteBuf worldBuffer = PacketByteBufs.create();
                    worldBuffer.writeUtf(name);
                    worldBuffer.writeUtf(composition.getCompost().getItem());
                    ServerPlayNetworking.send((ServerPlayer) player, ClientboundWorldPacket.ID, worldBuffer);
                });
                getNetworkCompositions().put(name, composition);
            });
        }
    }

}