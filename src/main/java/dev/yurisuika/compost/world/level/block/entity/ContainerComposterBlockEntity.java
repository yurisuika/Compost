package dev.yurisuika.compost.world.level.block.entity;

import dev.yurisuika.compost.Compost;
import dev.yurisuika.compost.mixin.world.level.block.ComposterBlockInvoker;
import dev.yurisuika.compost.world.level.block.ContainerComposterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.stream.IntStream;

public class ContainerComposterBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {

    public NonNullList<ItemStack> inventory = NonNullList.withSize(27 + 1, ItemStack.EMPTY);

    public ContainerComposterBlockEntity(BlockPos pos, BlockState state) {
        super(Compost.COMPOSTER.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (!trySaveLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, inventory);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!tryLoadLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, inventory);
        }
    }

    @Override
    public ItemStack getItem(int index) {
        return inventory.get(index);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(inventory, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            if (slot == 27) {
                stack.setCount(1);
            } else {
                stack.setCount(getMaxStackSize());
            }
        }
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void setItems(NonNullList<ItemStack> list) {
        inventory = list;
    }

    @Override
    public Component getDefaultName() {
        return new TranslatableComponent("container.compost.composter");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory) {
        return ChestMenu.threeRows(syncId, inventory, this);
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? IntStream.range(0, 27).toArray() : new int[]{27};
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return getBlockState().getValue(ContainerComposterBlock.LEVEL) < 7 && slot == 27 && getItem(27).isEmpty() && ContainerComposterBlock.COMPOSTABLES.containsKey(stack.getItem());
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.UP && getBlockState().getValue(ContainerComposterBlock.LEVEL) < 7 && slot == 27 && getItem(27).isEmpty() && ContainerComposterBlock.COMPOSTABLES.containsKey(stack.getItem());
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot < 27 && !stack.isEmpty();
    }

    @Override
    public void setChanged() {
        BlockState state = Objects.requireNonNull(getBlockState());
        ItemStack input = getItem(27);
        if (!input.isEmpty() && state.getValue(ContainerComposterBlock.LEVEL) < 7) {
            state = ComposterBlockInvoker.invokeAddItem(state, level, getBlockPos(), input);
            level.levelEvent(LevelEvent.COMPOSTER_FILL, getBlockPos(), state != getBlockState() ? 1 : 0);
            removeItemNoUpdate(27);
        }
        if (state.getValue(ContainerComposterBlock.LEVEL) == 8 && isEmpty()) {
            ContainerComposterBlock.empty(null, state, Objects.requireNonNull(level), getBlockPos());
        }
        super.setChanged();
    }

}