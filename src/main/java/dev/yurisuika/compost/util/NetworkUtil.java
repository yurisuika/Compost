package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.handler.CompostHandler;
import dev.yurisuika.compost.network.packet.s2c.CompostS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

import static dev.yurisuika.compost.config.CompostConfig.*;

public class NetworkUtil {

    public static List<ItemStack> stacks;

    public static void sendItems(World world, PlayerEntity player) {
        if (!world.isClient()) {
            Arrays.stream(config.levels).forEach(level -> {
                if (Objects.equals(level.name, Objects.requireNonNull(world.getServer()).getSaveProperties().getLevelName())) {
                    Arrays.stream(level.items).forEach(item -> {
                        ItemStack stack = CompostUtil.createItemStack(item);
                        stack.setCount(item.max);
                        CompostHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new CompostS2CPacket(ArrayUtils.indexOf(level.items, item), stack));
                    });
                }
            });
        }
    }

}