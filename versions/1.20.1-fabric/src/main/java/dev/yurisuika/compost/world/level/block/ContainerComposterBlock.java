package dev.yurisuika.compost.world.level.block;

import dev.yurisuika.compost.Compost;
import dev.yurisuika.compost.world.level.block.entity.ContainerComposterBlockEntity;
import dev.yurisuika.compost.world.level.storage.loot.CompostLootTables;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParamSets;
import dev.yurisuika.compost.world.level.storage.loot.parameters.CompostLootContextParams;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int fillLevel = state.getValue(LEVEL);
        ItemStack itemStack = player.getItemInHand(hand);
        if (fillLevel < 8 && COMPOSTABLES.containsKey(itemStack.getItem())) {
            if (fillLevel < 7 && !level.isClientSide()) {
                level.levelEvent(LevelEvent.COMPOSTER_FILL, pos, state != addItem(player, state, level, pos, itemStack) ? 1 : 0);
                player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (fillLevel == 8) {
            extractProduce(player, state, level, pos);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    public static BlockState insertItem(Entity sourceEntity, BlockState state, ServerLevel level, ItemStack stack, BlockPos pos) {
        int fillLevel = state.getValue(LEVEL);
        if (fillLevel < 7 && COMPOSTABLES.containsKey(stack.getItem())) {
            BlockState blockState = addItem(sourceEntity, state, level, pos, stack);
            stack.shrink(1);
            return blockState;
        } else {
            return state;
        }
    }

    public static BlockState addItem(Entity sourceEntity, BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack) {
        int fillLevel = state.getValue(LEVEL);
        float chance = COMPOSTABLES.getFloat(stack.getItem());
        if ((fillLevel != 0 || !(chance > 0.0F)) && !(level.getRandom().nextDouble() < (double) chance)) {
            return state;
        } else {
            if (level.getBlockEntity(pos) instanceof ContainerComposterBlockEntity blockEntity) {
                ItemStack input = stack.copy();
                input.setCount(1);
                blockEntity.compostables.add(input);
            }

            int newLevel = fillLevel + 1;
            BlockState newState = state.setValue(LEVEL, newLevel);
            level.setBlock(pos, newState, 3);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(sourceEntity, newState));
            if (newLevel == 7) {
                level.scheduleTick(pos, state.getBlock(), 20);
            }

            return newState;
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

                ResourceLocation resourceLocation = blockEntity.getLootTable();
                if (resourceLocation != null) {
                    LootTable lootTable = level.getServer().getLootData().getLootTable(resourceLocation);
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