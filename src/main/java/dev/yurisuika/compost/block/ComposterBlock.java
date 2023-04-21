package dev.yurisuika.compost.block;

import com.google.common.collect.Lists;
import dev.yurisuika.compost.Compost;
import dev.yurisuika.compost.block.entity.ComposterBlockEntity;
import dev.yurisuika.compost.mixin.block.ComposterBlockInvoker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class ComposterBlock extends net.minecraft.block.ComposterBlock implements BlockEntityProvider {

    public ComposterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ComposterBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int i = state.get(LEVEL);
        ItemStack itemStack = player.getStackInHand(hand);
        if (i < 8 && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemStack.getItem())) {
            if (i < 7 && !world.isClient) {
                world.syncWorldEvent(WorldEvents.COMPOSTER_USED, pos, state != ComposterBlockInvoker.invokeAddToComposter(player, state, world, pos, itemStack) ? 1 : 0);
                player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
            }
            return ActionResult.success(world.isClient);
        }
        if (i == 8) {
            ComposterBlock.emptyFullComposter(state, world, pos);
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    public static BlockState emptyFullComposter(BlockState state, World world, BlockPos pos) {
        if (!world.isClient) {
            for (int i = 0; i < 27; i++) {
                double x = (double)(world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                double y = (double)(world.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
                double z = (double)(world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + x, (double)pos.getY() + y, (double)pos.getZ() + z, ((ComposterBlockEntity)Objects.requireNonNull(world.getBlockEntity(pos))).inventory.get(i));
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }
        BlockState blockState = ComposterBlock.emptyComposter(state, world, pos);
        world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return blockState;
    }

    public static BlockState emptyComposter(BlockState state, WorldAccess world, BlockPos pos) {
        BlockState blockState = state.with(LEVEL, 0);
        world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
        return blockState;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LEVEL) == 7) {
            List<ItemStack> list = Lists.newArrayList();
            Arrays.stream(Compost.config.items).forEach(group -> {
                if(ThreadLocalRandom.current().nextDouble() < group.chance) {
                    list.add(Compost.createItemStack(group));
                }
            });
            Collections.shuffle(list);
            for (ItemStack itemStack : list) {
                ((ComposterBlockEntity)Objects.requireNonNull(world.getBlockEntity(pos))).inventory.set(list.indexOf(itemStack), itemStack).copy();
            }
            world.setBlockState(pos, state.cycle(LEVEL), Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

}