package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.handler.CompostHandler;
import dev.yurisuika.compost.network.packet.s2c.CompostS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

import static dev.yurisuika.compost.server.option.CompostConfig.*;

public class NetworkUtil {

    public static void sendGroups(World world, PlayerEntity player) {
        if (!world.isClient()) {
            Arrays.stream(config.worlds).forEach(level -> {
                if (Objects.equals(level.world, Objects.requireNonNull(world.getServer()).getSaveProperties().getLevelName())) {
                    Arrays.stream(level.items).forEach(group -> {
                        CompostHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new CompostS2CPacket(ArrayUtils.indexOf(level.items, group), group.item, group.chance, group.min, group.max));
                    });
                }
            });
        }
    }

}