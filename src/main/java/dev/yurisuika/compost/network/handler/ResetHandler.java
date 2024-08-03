package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.protocol.common.custom.ResetPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class ResetHandler {

    public static void register(RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar("compost")
                .versioned("1")
                .optional();

        registrar.play(ResetPayload.ID, ResetPayload::new, handler -> handler.client(ResetPayload::handle));
    }

}