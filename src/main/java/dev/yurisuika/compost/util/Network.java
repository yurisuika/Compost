package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.protocol.common.ClientboundCompostPacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundWorldPacket;
import dev.yurisuika.compost.world.Composition;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

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
            ClientboundResetPacket.CHANNEL.send(new ClientboundResetPacket(), PacketDistributor.PLAYER.with(player));
            COMPOSITIONS.clear();
            Configure.getCompositions().forEach((name, composition) -> {
                ClientboundCompostPacket.CHANNEL.send(new ClientboundCompostPacket(name, composition.getCompost().getItem(), composition.getCompost().getChance(), composition.getCompost().getCount().getMin(), composition.getCompost().getCount().getMax()), PacketDistributor.PLAYER.with(player));
                composition.getWorlds().forEach(world -> ClientboundWorldPacket.CHANNEL.send(new ClientboundWorldPacket(name, world), PacketDistributor.PLAYER.with(player)));
                COMPOSITIONS.put(name, composition);
            });
        }
    }

}