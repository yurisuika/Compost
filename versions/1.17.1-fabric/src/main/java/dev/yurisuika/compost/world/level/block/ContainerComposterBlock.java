package dev.yurisuika.compost.world.level.block;

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
import net.minecraft.world.*;
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
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

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
        int i = state.getValue(LEVEL);
        ItemStack itemStack = player.getItemInHand(hand);
        if (i < 8 && COMPOSTABLES.containsKey(itemStack.getItem())) {
            if (i < 7 && !level.isClientSide()) {
                level.levelEvent(LevelEvent.COMPOSTER_FILL, pos, state != addItem(state, level, pos, itemStack) ? 1 : 0);
                player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (i == 8) {
            extractProduce(state, level, pos);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    public static BlockState insertItem(BlockState state, ServerLevel level, ItemStack stack, BlockPos pos) {
        int i = state.getValue(LEVEL);
        if (i < 7 && COMPOSTABLES.containsKey(stack.getItem())) {
            BlockState blockstate = addItem(state, level, pos, stack);
            stack.shrink(1);
            return blockstate;
        } else {
            return state;
        }
    }

    public static BlockState addItem(BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack) {
        int i = state.getValue(LEVEL);
        float f = COMPOSTABLES.getFloat(stack.getItem());
        if ((i != 0 || !(f > 0.0F)) && !(level.getRandom().nextDouble() < (double) f)) {
            return state;
        } else {
            if (level.getBlockEntity(pos) instanceof ContainerComposterBlockEntity blockEntity) {
                blockEntity.compostables.add(stack);
            }
            int j = i + 1;
            BlockState blockstate = state.setValue(LEVEL, j);
            level.setBlock(pos, blockstate, 3);
            if (j == 7) {
                level.getBlockTicks().scheduleTick(pos, state.getBlock(), 20);
            }

            return blockstate;
        }
    }

    public static BlockState extractProduce(BlockState state, Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof ContainerComposterBlockEntity blockEntity) {
                for (int i = 0; i < 27; i++) {
                    double x = (double) (level.getRandom().nextFloat() * 0.7F) + 0.15000000596046448D;
                    double y = (double) (level.getRandom().nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
                    double z = (double) (level.getRandom().nextFloat() * 0.7F) + 0.15000000596046448D;
                    ItemEntity itemEntity = new ItemEntity(level, pos.getX() + x, pos.getY() + y, pos.getZ() + z, blockEntity.removeItemNoUpdate(i));
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
                blockEntity.setChanged();
            }
        }
        level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        return empty(state, level, pos);
    }

    public static BlockState empty(BlockState state, LevelAccessor level, BlockPos pos) {
        BlockState blockState = state.setValue(LEVEL, 0);
        level.setBlock(pos, blockState, Block.UPDATE_ALL);
        return blockState;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (state.getValue(LEVEL) == 7) {
            if (level.getBlockEntity(pos) instanceof ContainerComposterBlockEntity blockEntity) {
                blockEntity.setLootTable(CompostLootTables.COMPOSTER, random.nextLong());

                ResourceLocation resourceLocation = blockEntity.getLootTable();
                if (resourceLocation != null) {
                    LootTable lootTable = level.getServer().getLootTables().get(resourceLocation);
                    blockEntity.setLootTable(null);
                    LootContext.Builder builder = new LootContext.Builder(level)
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos));
                    if (!blockEntity.compostables.isEmpty()) {
                        for (ItemStack compostable : blockEntity.compostables) {
                            builder.withOptionalParameter(CompostLootContextParams.COMPOSTABLE, compostable);
                        }
                    }
                    lootTable.fill(blockEntity, builder.create(CompostLootContextParamSets.COMPOSTER));
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