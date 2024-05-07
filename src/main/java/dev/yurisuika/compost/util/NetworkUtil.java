package dev.yurisuika.compost.util;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Objects;

import static dev.yurisuika.compost.server.option.CompostConfig.config;

public class NetworkUtil {

    public static void sendGroups(World world, PlayerEntity player) {
        if (!world.isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            Arrays.stream(config.worlds).forEach(level -> {
                if (Objects.equals(level.world, Objects.requireNonNull(world.getServer()).getSaveProperties().getLevelName())) {
                    buf.writeInt(level.items.length);
                    Arrays.stream(level.items).forEach(group -> {
                        buf.writeString(group.item);
                        buf.writeDouble(group.chance);
                        buf.writeInt(group.min);
                        buf.writeInt(group.max);
                    });
                }
            });
            ServerPlayNetworking.send((ServerPlayerEntity)player, new Identifier("compost", "group"), buf);
        }
    }

}