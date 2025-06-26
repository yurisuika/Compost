package dev.yurisuika.compost.world.level.block.entity;

import dev.yurisuika.compost.mixin.world.level.block.ComposterBlockInvoker;
import dev.yurisuika.compost.world.level.block.ContainerComposterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.stream.IntStream;

public class ContainerComposterBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

    public NonNullList<ItemStack> items = NonNullList.withSize(27 + 1, ItemStack.EMPTY);

    public ContainerComposterBlockEntity(BlockPos pos, BlockState state) {
        super(CompostBlockEntityType.COMPOSTER, pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, items);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
    }

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (slot == 27) {
            stack.setCount(1);
            setChanged();
        } else if (stack.getCount() > 64) {
            stack.setCount(64);
        }
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : items) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("container.compost.composter");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory) {
        return ChestMenu.threeRows(syncId, inventory, this);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
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
        BlockState state = getBlockState();
        ItemStack input = getItem(27);
        if (!input.isEmpty() && state.getValue(ContainerComposterBlock.LEVEL) < 7) {
            state = ComposterBlockInvoker.invokeAddItem(null, getBlockState(), getLevel(), getBlockPos(), input);
            getLevel().levelEvent(LevelEvent.COMPOSTER_FILL, getBlockPos(), state != getBlockState() ? 1 : 0);
            removeItemNoUpdate(27);
        }
        if (state.getValue(ContainerComposterBlock.LEVEL) == 8 && isEmpty()) {
            ContainerComposterBlock.empty(null, getBlockState(), getLevel(), getBlockPos());
        }
        super.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

}