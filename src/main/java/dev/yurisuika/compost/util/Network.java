package dev.yurisuika.compost.util;

import dev.yurisuika.compost.util.config.Option;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Network {

    public static List<ItemStack> stacks = new ArrayList<>();

    public static List<ItemStack> getStacks() {
        return stacks;
    }

    public static void setStacks(List<ItemStack> stacks) {
        Network.stacks = stacks;
    }

    public static void sendProduce(Level level, Player player) {
        if (!level.isClientSide()) {
            FriendlyByteBuf reset = PacketByteBufs.create();
            ServerPlayNetworking.send((ServerPlayer) player, ResourceLocation.tryParse("compost:reset"), reset);
            Option.getWorlds().forEach(world -> {
                if (Objects.equals(world.getName(), Objects.requireNonNull(level.getServer()).getWorldData().getLevelName())) {
                    world.getProduce().forEach(produce -> {
                        FriendlyByteBuf buffer = PacketByteBufs.create();
                        buffer.writeUtf(produce.getItem());
                        buffer.writeDouble(produce.getChance());
                        buffer.writeInt(produce.getMin());
                        buffer.writeInt(produce.getMax());
                        ServerPlayNetworking.send((ServerPlayer) player, ResourceLocation.tryParse("compost:produce"), buffer);
                    });
                }
            });
        }
    }

}