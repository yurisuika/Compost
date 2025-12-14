package dev.yurisuika.compost.world.level.block;

import dev.yurisuika.compost.Compost;
import dev.yurisuika.compost.world.level.block.entity.ContainerComposterBlockEntity;
import dev.yurisuika.compost.world.level.storage.loot.CompostLootTables;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParams;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

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
        if (i < 8 && getValue(stack) > 0.0F) {
            if (i < 7 && !level.isClientSide()) {
                level.levelEvent(LevelEvent.COMPOSTER_FILL, pos, state != addItem(player, state, level, pos, stack) ? 1 : 0);
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                stack.consume(1, player);
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

    public static BlockState insertItem(Entity entity, BlockState state, ServerLevel level, ItemStack stack, BlockPos pos) {
        int i = state.getValue(LEVEL);
        if (i < 7 && getValue(stack) > 0.0F) {
            BlockState blockstate = addItem(entity, state, level, pos, stack);
            stack.shrink(1);
            return blockstate;
        } else {
            return state;
        }
    }

    public static BlockState addItem(Entity entity, BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack) {
        int i = state.getValue(LEVEL);
        float f = getValue(stack);
        if ((i != 0 || !(f > 0.0F)) && !(level.getRandom().nextDouble() < (double) f)) {
            return state;
        } else {
            if (level.getBlockEntity(pos) instanceof ContainerComposterBlockEntity blockEntity) {
                blockEntity.compostables.add(stack);
            }
            int j = i + 1;
            BlockState blockstate = state.setValue(LEVEL, j);
            level.setBlock(pos, blockstate, 3);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
            if (j == 7) {
                level.scheduleTick(pos, state.getBlock(), 20);
            }

            return blockstate;
        }
    }

    public static BlockState extractProduce(Entity entity, BlockState state, Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof ContainerComposterBlockEntity blockEntity) {
                for (int i = 0; i < 27; i++) {
                    Vec3 vec3 = Vec3.atLowerCornerWithOffset(pos, 0.5D, 1.01D, 0.5D).offsetRandom(level.random, 0.7F);
                    ItemEntity itemEntity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), blockEntity.removeItemNoUpdate(i));
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
                blockEntity.setChanged();
            }
        }
        level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        return empty(entity, state, level, pos);
    }

    public static BlockState empty(Entity entity, BlockState state, LevelAccessor level, BlockPos pos) {
        BlockState blockState = state.setValue(LEVEL, 0);
        level.setBlock(pos, blockState, Block.UPDATE_ALL);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockState));
        return blockState;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(LEVEL) == 7) {
            if (level.getBlockEntity(pos) instanceof ContainerComposterBlockEntity blockEntity) {
                blockEntity.setLootTable(CompostLootTables.COMPOSTERS_COMPOST, random.nextLong());

                ResourceKey<LootTable> resourceKey = blockEntity.getLootTable();
                if (resourceKey != null) {
                    LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(resourceKey);
                    blockEntity.setLootTable(null);
                    LootParams.Builder builder = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos));
                    if (!blockEntity.compostables.isEmpty()) {
                        builder.withOptionalParameter(CompostLootContextParams.COMPOSTABLES, blockEntity.compostables);
                    } else {
                        Compost.LOGGER.warn("Composter has had no compostables added and will not be able to match against them!");
                    }
                    lootTable.fill(blockEntity, builder.create(CompostLootContextParamSets.COMPOSTER), blockEntity.getLootTableSeed());
                }

                blockEntity.compostables.clear();

                level.setBlock(pos, state.cycle(LEVEL), Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
                blockEntity.setChanged();
            }
        }
    }

    @Override
    public WorldlyContainer getContainer(BlockState state, LevelAccessor level, BlockPos pos) {
        return (WorldlyContainer) level.getBlockEntity(pos);
    }

}