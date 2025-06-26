package dev.yurisuika.compost.mixin.server;

import dev.yurisuika.compost.server.event.ServerStartedEvent;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;updateStatusIcon(Lnet/minecraft/network/protocol/status/ServerStatus;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void onRun(CallbackInfo info) {
        (ServerStartedEvent.SERVER_STARTED.invoker()).onServerStarted((MinecraftServer) (Object) this);
    }

}