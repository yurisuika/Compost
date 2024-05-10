package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.handler.CompostHandler;
import dev.yurisuika.compost.network.packet.s2c.CompostS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dev.yurisuika.compost.config.CompostConfig.*;

public class NetworkUtil {

    public static List<ItemStack> stacks;

    public static void sendItems(World world, PlayerEntity player) {
        if (!world.isClient()) {
            List<ItemStack> stacks = new ArrayList<>();
            Arrays.stream(config.levels).forEach(level -> {
                if (Objects.equals(level.name, Objects.requireNonNull(world.getServer()).getSaveProperties().getLevelName())) {
                    Arrays.stream(level.items).forEach(item -> {
                        ItemStack stack = CompostUtil.createItemStack(item);
                        stack.setCount(item.max);
                        stacks.add(stack);
                    });
                }
            });
            CompostHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new CompostS2CPacket(stacks));
        }
    }

}