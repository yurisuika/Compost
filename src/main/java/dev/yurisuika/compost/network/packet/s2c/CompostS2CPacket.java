package dev.yurisuika.compost.network.packet.s2c;

import dev.yurisuika.compost.util.NetworkUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.List;

public record CompostS2CPacket(List<ItemStack> stacks) implements CustomPayload {

    public static final Identifier ID = new Identifier("compost", "items");

    public CompostS2CPacket(PacketByteBuf buf) {
        this(buf.readList(PacketByteBuf::readItemStack));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeCollection(stacks, PacketByteBuf::writeItemStack);
    }

    @Override
    public Identifier id() {
        return ID;
    }

    public static void handle(final CompostS2CPacket message, PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            NetworkUtil.stacks = message.stacks;
        });
    }

}