package dev.yurisuika.compost.mixin.minecraft.world.level.block;

import dev.yurisuika.compost.world.level.block.ContainerComposterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;

@Mixin(Blocks.class)
public abstract class BlocksMixin {

    @ModifyArg(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=composter")), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 0), index = 1)
    private static Function<Properties, Block> replaceComposter(Function<Properties, Block> function) {
        return ContainerComposterBlock::new;
    }

}