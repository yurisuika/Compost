package dev.yurisuika.compost.world.level.block;

import dev.yurisuika.compost.mixin.world.level.block.ComposterBlockInvoker;
import dev.yurisuika.compost.util.Parse;
import dev.yurisuika.compost.util.config.Option;
import dev.yurisuika.compost.world.level.block.entity.ContainerComposterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ContainerComposterBlock extends ComposterBlock implements EntityBlock {

    public ContainerComposterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ContainerComposterBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof Container) {
                Containers.dropContents(level, pos, (Container) blockentity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int i = state.getValue(LEVEL);
        if (i < 8 && COMPOSTABLES.containsKey(stack.getItem())) {
            if (i < 7 && !level.isClientSide) {
                level.levelEvent(LevelEvent.COMPOSTER_FILL, pos, state != ComposterBlockInvoker.invokeAddItem(player, state, level, pos, stack) ? 1 : 0);
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (state.getValue(LEVEL) == 8) {
            extractProduce(player, state, level, pos);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public static BlockState extractProduce(Entity user, BlockState state, Level level, BlockPos pos) {
        if (!level.isClientSide) {
            ContainerComposterBlockEntity blockEntity = (ContainerComposterBlockEntity) level.getBlockEntity(pos);
            for (int i = 0; i < 27; i++) {
                Vec3 vec3 = Vec3.atLowerCornerWithOffset(pos, 0.5D, 1.01D, 0.5D).offsetRandom(level.random, 0.7F);
                ItemEntity itemEntity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), blockEntity.removeItemNoUpdate(i));
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
            blockEntity.setChanged();
        }
        level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        return empty(user, state, level, pos);
    }

    public static BlockState empty(Entity user, BlockState state, LevelAccessor level, BlockPos pos) {
        BlockState blockState = state.setValue(LEVEL, 0);
        level.setBlock(pos, blockState, Block.UPDATE_ALL);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(user, blockState));
        return blockState;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(LEVEL) == 7) {
            ContainerComposterBlockEntity blockEntity = (ContainerComposterBlockEntity) level.getBlockEntity(pos);
            List<ItemStack> list = new ArrayList<>();
            Option.getWorld(level.getServer().getWorldData().getLevelName()).getProduce().forEach(produce -> {
                if (ThreadLocalRandom.current().nextDouble() < produce.getChance()) {
                    list.add(Parse.createItemStack(level.getServer().registryAccess(), produce));
                }
            });
            Collections.shuffle(list);
            for (ItemStack itemStack : list) {
                blockEntity.setItem(list.indexOf(itemStack), itemStack);
            }
            level.setBlock(pos, state.cycle(LEVEL), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
            blockEntity.setChanged();
        }
    }

    @Override
    public WorldlyContainer getContainer(BlockState state, LevelAccessor level, BlockPos pos) {
        return (WorldlyContainer) level.getBlockEntity(pos);
    }

}