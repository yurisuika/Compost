package dev.yurisuika.compost.block;

import com.google.common.collect.Lists;
import dev.yurisuika.compost.block.entity.ComposterBlockEntity;
import dev.yurisuika.compost.mixin.block.ComposterBlockInvoker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static dev.yurisuika.compost.client.option.CompostConfig.*;

public class ComposterBlock extends net.minecraft.block.ComposterBlock implements BlockEntityProvider {

    public ComposterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ComposterBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockentity = world.getBlockEntity(pos);
            if (blockentity instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory)blockentity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int i = state.get(LEVEL);
        if (i < 8 && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(stack.getItem())) {
            if (i < 7 && !world.isClient) {
                world.syncWorldEvent(WorldEvents.COMPOSTER_USED, pos, state != ComposterBlockInvoker.invokeAddToComposter(player, state, world, pos, stack) ? 1 : 0);
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
            }
            return ItemActionResult.success(world.isClient);
        } else {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.get(LEVEL) == 8) {
            emptyFullComposter(player, state, world, pos);
            return ActionResult.success(world.isClient);
        } else {
            return ActionResult.PASS;
        }
    }

    public static BlockState emptyFullComposter(Entity user, BlockState state, World world, BlockPos pos) {
        if (!world.isClient) {
            ComposterBlockEntity blockEntity = (ComposterBlockEntity)Objects.requireNonNull(world.getBlockEntity(pos));
            for (int i = 0; i < 27; i++) {
                Vec3d vec3d = Vec3d.add(pos, 0.5, 1.01, 0.5).addRandom(world.random, 0.7F);
                ItemEntity itemEntity = new ItemEntity(world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), blockEntity.removeStack(i));
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
            blockEntity.markDirty();
        }
        world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return emptyComposter(user, state, world, pos);
    }

    public static BlockState emptyComposter(Entity user, BlockState state, WorldAccess world, BlockPos pos) {
        BlockState blockState = state.with(LEVEL, 0);
        world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(user, blockState));
        return blockState;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LEVEL) == 7) {
            ComposterBlockEntity blockEntity = (ComposterBlockEntity)Objects.requireNonNull(world.getBlockEntity(pos));
            List<ItemStack> list = Lists.newArrayList();
            Arrays.stream(config.items).forEach(group -> {
                if (ThreadLocalRandom.current().nextDouble() < group.chance) {
                    list.add(createItemStack(group));
                }
            });
            Collections.shuffle(list);
            for (ItemStack itemStack : list) {
                blockEntity.setStack(list.indexOf(itemStack), itemStack);
            }
            world.setBlockState(pos, state.cycle(LEVEL), Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            blockEntity.markDirty();
        }
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return (SidedInventory)world.getBlockEntity(pos);
    }

}