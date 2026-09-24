package dev.yurisuika.compost.world.level.block;

import dev.yurisuika.compost.Compost;
import dev.yurisuika.compost.world.level.block.entity.ContainerComposterBlockEntity;
import dev.yurisuika.compost.world.level.storage.loot.CompostLootTables;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParams;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class ContainerComposterBlock extends ComposterBlock implements EntityBlock {

    public ContainerComposterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ContainerComposterBlockEntity(pos, state);
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean moved) {
        if (!moved) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof Container) {
                Containers.dropContents(level, pos, (Container) blockentity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int fillLevel = state.getValue(LEVEL);
        Compostable compostable = stack.get(DataComponents.COMPOSTABLE);
        if (fillLevel < 8 && compostable != null) {
            if (fillLevel < 7 && !level.isClientSide()) {
                level.levelEvent(LevelEvent.COMPOSTER_FILL, pos, state != addLayer(player, state, (ServerLevel) level, pos, compostable, stack) ? 1 : 0);
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
        int fillLevel = state.getValue(LEVEL);
        if (fillLevel == 8) {
            extractProduce(player, state, level, pos);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public static BlockState insertItem(Entity sourceEntity, BlockState state, ServerLevel level, ItemStack stack, BlockPos pos) {
        int fillLevel = state.getValue(LEVEL);
        Compostable compostable = stack.get(DataComponents.COMPOSTABLE);
        if (fillLevel < 7 && compostable != null) {
            BlockState blockState = addLayer(sourceEntity, state, level, pos, compostable, stack);
            stack.shrink(1);
            return blockState;
        } else {
            return state;
        }
    }

    public static BlockState addLayer(Entity sourceEntity, BlockState state, ServerLevel level, BlockPos pos, Compostable compostable, ItemStack stack) {
        int fillLevel = state.getValue(LEVEL);
        LootContext lootContext = (new LootContext.Builder((new LootParams.Builder(level)).withParameter(LootContextParams.BLOCK_STATE, state).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withOptionalParameter(LootContextParams.INTERACTING_ENTITY, sourceEntity).create(LootContextParamSets.BLOCK_INTERACT))).create(Optional.empty());
        int layersToAdd = compostable.layers().get(lootContext, 0);
        if (layersToAdd > 0) {
            if (level.getBlockEntity(pos) instanceof ContainerComposterBlockEntity blockEntity) {
                ItemStack input = stack.copy();
                input.setCount(1);
                blockEntity.compostables.add(input);
            }

            int newLevel = Mth.clamp(fillLevel + layersToAdd, 0, 7);
            BlockState newState = state.setValue(LEVEL, newLevel);
            level.setBlockAndUpdate(pos, newState);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(sourceEntity, newState));
            if (newLevel == 7) {
                level.scheduleTick(pos, state.getBlock(), 20);
            }

            return newState;
        } else {
            return state;
        }
    }

    public static BlockState extractProduce(Entity sourceEntity, BlockState state, Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof ContainerComposterBlockEntity blockEntity) {
                for (int slot = 0; slot < ContainerComposterBlockEntity.INPUT_SLOT; slot++) {
                    Vec3 vec3 = Vec3.atLowerCornerWithOffset(pos, 0.5D, 1.01D, 0.5D).offsetRandom(level.getRandom(), 0.7F);
                    ItemEntity itemEntity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), blockEntity.removeItemNoUpdate(slot));
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
                blockEntity.setChanged();
            }
        }
        level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        return empty(sourceEntity, state, level, pos);
    }

    public static BlockState empty(Entity sourceEntity, BlockState state, LevelAccessor level, BlockPos pos) {
        BlockState blockState = state.setValue(LEVEL, 0);
        level.setBlock(pos, blockState, Block.UPDATE_ALL);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(sourceEntity, blockState));
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
                        Compost.LOGGER.warn("Composter at x={} y={} z={} has been filled with loot generated using an empty compostables list. It may not contain any compost!", pos.getX(), pos.getY(), pos.getZ());
                    }
                    SimpleContainer lootContainer = new SimpleContainer(ContainerComposterBlockEntity.OUTPUT_SIZE);
                    lootTable.fill(lootContainer, builder.create(CompostLootContextParamSets.COMPOSTER), blockEntity.getLootTableSeed());
                    for (int slot = 0; slot <ContainerComposterBlockEntity.INPUT_SLOT; slot++) {
                        blockEntity.setItem(slot, lootContainer.getItem(slot).copy());
                    }
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