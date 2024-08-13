package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.protocol.common.ClientboundProducePacket;
import dev.yurisuika.compost.network.protocol.common.ClientboundResetPacket;
import dev.yurisuika.compost.util.config.Option;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.PacketDistributor;

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
            ClientboundResetPacket.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ClientboundResetPacket());
            Option.getWorlds().forEach(world -> {
                if (Objects.equals(world.getName(), level.getServer().getWorldData().getLevelName())) {
                    world.getProduce().forEach(produce -> {
                        ClientboundProducePacket.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ClientboundProducePacket(produce.getItem(), produce.getChance(), produce.getMin(), produce.getMax()));
                    });
                }
            });
        }
    }

}