package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.protocol.common.custom.ProducePayload;
import dev.yurisuika.compost.network.protocol.common.custom.ResetPayload;
import dev.yurisuika.compost.util.config.Option;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

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
            PacketDistributor.PLAYER.with((ServerPlayer) player).send(new ResetPayload());
            Option.getWorlds().forEach(world -> {
                if (Objects.equals(world.getName(), Objects.requireNonNull(level.getServer()).getWorldData().getLevelName())) {
                    world.getProduce().forEach(produce -> {
                        PacketDistributor.PLAYER.with((ServerPlayer) player).send(new ProducePayload(produce.getItem(), produce.getChance(), produce.getMin(), produce.getMax()));
                    });
                }
            });
        }
    }

}