package dev.yurisuika.compost.mixin.mods;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("UnstableApiUsage")
@Mixin(ItemStorage.class)
public class FabricTransferAPIMixin {
    @Redirect(method = "<clinit>", at = @At(
            value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/lookup/v1/block/BlockApiLookup;registerForBlocks(Lnet/fabricmc/fabric/api/lookup/v1/block/BlockApiLookup$BlockApiProvider;[Lnet/minecraft/block/Block;)V",
            ordinal = 0
    ))
    private static void skipRegisterComposter(BlockApiLookup<?, ?> instance, BlockApiLookup.BlockApiProvider<?, ?> acBlockApiProvider, Block[] blocks) {
        // Do not register the composter with Fabric Transfer API for special handling,
        // because we have given it a real Inventory which the special handling breaks.
    }
}
