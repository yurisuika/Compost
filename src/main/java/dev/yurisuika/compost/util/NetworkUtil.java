package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.packet.s2c.CompostPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dev.yurisuika.compost.config.CompostConfig.*;

public class NetworkUtil {

    public static List<ItemStack> stacks;

    public static void sendItems(World world, PlayerEntity player) {
        if (!world.isClient()) {
            List<String> names = new ArrayList<>();
            List<Double> chances = new ArrayList<>();
            List<Integer> mins = new ArrayList<>();
            List<Integer> maxes = new ArrayList<>();
            Arrays.stream(config.levels).forEach(level -> {
                if (Objects.equals(level.name, Objects.requireNonNull(world.getServer()).getSaveProperties().getLevelName())) {
                    Arrays.stream(level.items).forEach(item -> {
                        names.add(item.name);
                        chances.add(item.chance);
                        mins.add(item.min);
                        maxes.add(item.max);
                    });
                }
            });
            ServerPlayNetworking.send((ServerPlayerEntity)player, new CompostPayload(names, chances, mins, maxes));
        }
    }

}