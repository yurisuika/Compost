package dev.yurisuika.compost.mixin.block;

import dev.yurisuika.compost.block.ComposterBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class BlocksMixin {

    @Redirect(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=composter")), at = @At(value = "NEW", target = "net/minecraft/block/ComposterBlock", ordinal = 0))
    private static net.minecraft.block.ComposterBlock redirectComposter(AbstractBlock.Settings settings) {
        return new ComposterBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(Instrument.BASS).strength(0.6f).sounds(BlockSoundGroup.WOOD).burnable());
    }

}