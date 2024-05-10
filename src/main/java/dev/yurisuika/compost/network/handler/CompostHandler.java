package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.packet.s2c.CompostS2CPacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class CompostHandler {

    public static void register(RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar("compost")
                .versioned("1")
                .optional();
        registrar.play(CompostS2CPacket.ID,
                CompostS2CPacket::new,
                handler -> handler.client(CompostS2CPacket::handle));
    }

}