package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.protocol.common.custom.ProducePayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ProduceHandler {

    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("compost")
                .versioned("1")
                .optional();

        registrar.playToClient(ProducePayload.TYPE, ProducePayload.STREAM_CODEC, ProducePayload::handle);
    }

}