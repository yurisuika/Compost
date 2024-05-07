package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.packet.s2c.custom.CompostPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dev.yurisuika.compost.server.option.CompostConfig.*;

public class NetworkUtil {

    public static void sendGroups(World world, PlayerEntity player) {
        if (!world.isClient()) {
            List<String> item = new ArrayList<>();
            List<Double> chance = new ArrayList<>();
            List<Integer> min = new ArrayList<>();
            List<Integer> max = new ArrayList<>();
            Arrays.stream(config.worlds).forEach(level -> {
                if (Objects.equals(level.world, Objects.requireNonNull(world.getServer()).getSaveProperties().getLevelName())) {
                    Arrays.stream(level.items).forEach(group -> {
                        item.add(group.item);
                        chance.add(group.chance);
                        min.add(group.min);
                        max.add(group.max);
                    });
                }
            });
            ServerPlayNetworking.send((ServerPlayerEntity)player, new CompostPayload(item, chance, min, max));
        }
    }

}