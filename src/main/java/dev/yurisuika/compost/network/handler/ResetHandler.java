package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.protocol.common.custom.ResetPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ResetHandler {

    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("compost")
                .versioned("1")
                .optional();

        registrar.playToClient(ResetPayload.TYPE, ResetPayload.STREAM_CODEC, ResetPayload::handle);
    }

}