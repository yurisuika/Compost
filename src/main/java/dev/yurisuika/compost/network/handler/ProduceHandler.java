package dev.yurisuika.compost.network.handler;

import dev.yurisuika.compost.network.protocol.common.custom.ProducePayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class ProduceHandler {

    public static void register(RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar("compost")
                .versioned("1")
                .optional();

        registrar.play(ProducePayload.ID, ProducePayload::new, handler -> handler.client(ProducePayload::handle));
    }

}