package dev.yurisuika.compost.mixin.minecraft.world.level.block;

import dev.yurisuika.compost.Compost;
import dev.yurisuika.compost.world.level.block.ContainerComposterBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class BlocksMixin {

    @Redirect(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=composter")), at = @At(value = "NEW", target = "net/minecraft/world/level/block/ComposterBlock", ordinal = 0))
    private static ComposterBlock replaceComposter(Properties properties) {
        Compost.LOGGER.info("Replacing the vanilla composter with a Compost composter!");
        return new ContainerComposterBlock(Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(0.6F).sound(SoundType.WOOD).ignitedByLava());
    }

}