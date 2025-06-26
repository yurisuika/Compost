package dev.yurisuika.compost.mixin.server.players;

import dev.yurisuika.compost.server.event.ServerJoinEvent;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/bossevents/CustomBossEvents;onPlayerConnect(Lnet/minecraft/server/level/ServerPlayer;)V", shift = At.Shift.AFTER))
    private void onJoin(Connection connection, ServerPlayer player, CallbackInfo ci) {
        (ServerJoinEvent.JOIN.invoker()).onJoin((PlayerList) (Object) this, connection, player);
    }

}