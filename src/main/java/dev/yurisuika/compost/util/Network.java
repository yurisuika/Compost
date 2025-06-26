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
            ClientboundResetPacket.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundResetPacket());
            COMPOSITIONS.clear();
            Configure.getCompositions().forEach((name, composition) -> {
                ClientboundCompostPacket.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundCompostPacket(name, composition.getCompost().getItem(), composition.getCompost().getChance(), composition.getCompost().getCount().getMin(), composition.getCompost().getCount().getMax()));
                composition.getWorlds().forEach(world -> ClientboundWorldPacket.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundWorldPacket(name, world)));
                COMPOSITIONS.put(name, composition);
            });
        }
    }

}