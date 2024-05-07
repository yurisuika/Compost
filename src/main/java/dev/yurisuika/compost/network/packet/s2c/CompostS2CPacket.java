package dev.yurisuika.compost.network.packet.s2c;

import dev.yurisuika.compost.server.option.CompostConfig;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.Supplier;

import static dev.yurisuika.compost.Compost.*;

public class CompostS2CPacket {

    public int index;
    public String item;
    public Double chance;
    public Integer min;
    public Integer max;

    public CompostS2CPacket(int index, String item, Double chance, Integer min, Integer max) {
        this.index = index;
        this.item = item;
        this.chance = chance;
        this.min = min;
        this.max = max;
    }

    public static void encode(CompostS2CPacket packet, PacketByteBuf buf) {
        buf.writeInt(packet.index);
        buf.writeString(packet.item);
        buf.writeDouble(packet.chance);
        buf.writeInt(packet.min);
        buf.writeInt(packet.max);
    }

    public static CompostS2CPacket decode(PacketByteBuf buf) {
        return new CompostS2CPacket(buf.readInt(), buf.readString(), buf.readDouble(), buf.readInt(), buf.readInt());
    }

    public static void handle(final CompostS2CPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (message.index == 0) {
                GROUPS = new CompostConfig.Config.World.Group[0];
            }
            GROUPS = ArrayUtils.add(GROUPS, new CompostConfig.Config.World.Group(message.item, message.chance, message.min, message.max));
        });
        context.get().setPacketHandled(true);
    }

}