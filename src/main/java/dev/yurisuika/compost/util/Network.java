package dev.yurisuika.compost.util;

import dev.yurisuika.compost.network.protocol.common.custom.ProducePayload;
import dev.yurisuika.compost.network.protocol.common.custom.ResetPayload;
import dev.yurisuika.compost.util.config.Option;
import dev.yurisuika.compost.util.config.options.Produce;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
            ServerPlayNetworking.send((ServerPlayer) player, new ResetPayload());
            setStacks(new ArrayList<>());
            setProduce(new ArrayList<>());
            Option.getWorlds().forEach(world -> {
                if (Objects.equals(world.getName(), level.getServer().getWorldData().getLevelName())) {
                    world.getProduce().forEach(produce -> {
                        ServerPlayNetworking.send((ServerPlayer) player, new ProducePayload(produce.getItem(), produce.getChance(), produce.getMin(), produce.getMax()));
                        getStacks().add(Parse.createItemStack(level.getServer().registryAccess(), produce));
                        getProduce().add(produce);
                    });
                }
            });
        }
    }

}