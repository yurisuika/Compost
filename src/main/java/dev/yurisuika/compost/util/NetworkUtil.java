package dev.yurisuika.compost.util;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dev.yurisuika.compost.config.CompostConfig.*;

public class NetworkUtil {

    public static List<ItemStack> stacks;

    public static void sendItems(World world, PlayerEntity player) {
        if (!world.isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            Arrays.stream(config.levels).forEach(level -> {
                if (Objects.equals(level.name, Objects.requireNonNull(world.getServer()).getSaveProperties().getLevelName())) {
                    buf.writeInt(level.items.length);
                    Arrays.stream(level.items).forEach(item -> {
                        ItemStack stack = CompostUtil.createItemStack(item);
                        stack.setCount(item.max);
                        buf.writeItemStack(stack);
                    });
                }
            });
            ServerPlayNetworking.send((ServerPlayerEntity)player, new Identifier("compost", "items"), buf);
        }
    }

}