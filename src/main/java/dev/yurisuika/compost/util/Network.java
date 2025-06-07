package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.protocol.common.custom.CompostPayload;
import dev.yurisuika.compost.network.protocol.common.custom.ResetPayload;
import dev.yurisuika.compost.network.protocol.common.custom.WorldPayload;
import dev.yurisuika.compost.util.config.Option;
import dev.yurisuika.compost.util.config.options.Composition;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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

    public static void sendCompositions(Level level, Player player) {
        if (!level.isClientSide()) {
            ServerPlayNetworking.send((ServerPlayer) player, new ResetPayload());
            COMPOSITIONS.clear();
            Option.getCompositions().forEach((name, composition) -> {
                ServerPlayNetworking.send((ServerPlayer) player, new CompostPayload(name, composition.getCompost().getItem(), composition.getCompost().getChance(), composition.getCompost().getCount().getMin(), composition.getCompost().getCount().getMax()));
                composition.getWorlds().forEach(world -> ServerPlayNetworking.send((ServerPlayer) player, new WorldPayload(name, world)));
                COMPOSITIONS.put(name, composition);
            });
        }
    }

}