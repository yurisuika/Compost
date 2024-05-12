package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.packet.s2c.CompostPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;


public class CompostHandler {

    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("compost")
                .versioned("1")
                .optional();
        registrar.playToClient(CompostPayload.ID, CompostPayload.CODEC, CompostPayload::handle);
    }

}