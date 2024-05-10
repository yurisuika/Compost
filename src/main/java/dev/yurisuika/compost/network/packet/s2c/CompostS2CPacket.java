package dev.yurisuika.compost.network.packet.s2c;

import dev.yurisuika.compost.util.NetworkUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public class CompostS2CPacket {

    public int index;
    public ItemStack stack;

    public CompostS2CPacket(int index, ItemStack stack) {
        this.index = index;
        this.stack = stack;
    }

    public static void encode(CompostS2CPacket packet, PacketByteBuf buf) {
        buf.writeInt(packet.index);
        buf.writeItemStack(packet.stack);
    }

    public static CompostS2CPacket decode(PacketByteBuf buf) {
        return new CompostS2CPacket(buf.readInt(), buf.readItemStack());
    }

    public static void handle(final CompostS2CPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (message.index == 0) {
                NetworkUtil.stacks = new ArrayList<>();
            }
            NetworkUtil.stacks.add(message.stack);
        });
        context.get().setPacketHandled(true);
    }

}