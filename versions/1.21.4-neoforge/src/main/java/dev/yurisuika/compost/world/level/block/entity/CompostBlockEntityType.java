package dev.yurisuika.compost.world.level.block.entity;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public class CompostBlockEntityType {

    public static final BlockEntityType<ContainerComposterBlockEntity> COMPOSTER = new BlockEntityType<>(ContainerComposterBlockEntity::new, Set.of(Blocks.COMPOSTER));

}