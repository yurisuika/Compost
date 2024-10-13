package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.protocol.common.ClientboundProducePacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
import dev.yurisuika.compost.util.config.Option;
import dev.yurisuika.compost.util.config.options.Produce;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Network {

    public static List<ItemStack> stacks = new ArrayList<>();
    public static List<Produce> produce = new ArrayList<>();

    public static List<ItemStack> getStacks() {
        return stacks;
    }

    public static List<Produce> getProduce() {
        return produce;
    }

    public static void setStacks(List<ItemStack> stacks) {
        Network.stacks = stacks;
    }

    public static void setProduce(List<Produce> produce) {
        Network.produce = produce;
    }

    public static void sendProduce(Level level, Player player) {
        if (!level.isClientSide()) {
            ServerPlayNetworking.send((ServerPlayer) player, ClientboundResetPacket.ID, PacketByteBufs.empty());
            setStacks(new ArrayList<>());
            setProduce(new ArrayList<>());
            Option.getWorlds().forEach(world -> {
                if (Objects.equals(world.getName(), level.getServer().getWorldData().getLevelName())) {
                    world.getProduce().forEach(produce -> {
                        FriendlyByteBuf buffer = PacketByteBufs.create();
                        buffer.writeUtf(produce.getItem());
                        buffer.writeDouble(produce.getChance());
                        buffer.writeInt(produce.getMin());
                        buffer.writeInt(produce.getMax());
                        ServerPlayNetworking.send((ServerPlayer) player, ClientboundProducePacket.ID, buffer);
                        getStacks().add(Parse.createItemStack(produce));
                        getProduce().add(produce);
                    });
                }
            });
        }
    }

}