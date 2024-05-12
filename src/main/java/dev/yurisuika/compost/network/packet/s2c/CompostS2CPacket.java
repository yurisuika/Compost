package dev.yurisuika.compost.network.packet.s2c;

import dev.yurisuika.compost.util.NetworkUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class CompostS2CPacket {

    public List<ItemStack> stacks;

    public CompostS2CPacket(List<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public static void encode(CompostS2CPacket packet, PacketByteBuf buf) {
        buf.writeCollection(packet.stacks, PacketByteBuf::writeItemStack);
    }

    public static CompostS2CPacket decode(PacketByteBuf buf) {
        return new CompostS2CPacket(buf.readList(PacketByteBuf::readItemStack));
    }

    public static void handle(final CompostS2CPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            NetworkUtil.stacks = message.stacks;
        });
        context.get().setPacketHandled(true);
    }

}