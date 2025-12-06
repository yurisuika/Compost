package dev.yurisuika.compost.world.level.block.entity;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CompostBlockEntityType {

    public static final BlockEntityType<ContainerComposterBlockEntity> COMPOSTER = BlockEntityType.Builder.of(ContainerComposterBlockEntity::new, Blocks.COMPOSTER).build(null);

}