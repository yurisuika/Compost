package dev.yurisuika.compost.world.level.block.entity;

import dev.yurisuika.compost.world.level.block.ContainerComposterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ContainerComposterBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {

    public NonNullList<ItemStack> compost = NonNullList.withSize(27 + 1, ItemStack.EMPTY);
    public List<ItemStack> compostables = new ArrayList<>();

    public ContainerComposterBlockEntity(BlockPos pos, BlockState state) {
        super(CompostBlockEntityType.COMPOSTER, pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider holderProvider) {
        super.loadAdditional(tag, holderProvider);
        this.compost = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, compost, holderProvider);
        }

        ListTag listTag = tag.getListOrEmpty("Compostables");
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompoundOrEmpty(i);
            compostables.add(ItemStack.parse(holderProvider, compoundTag).orElse(ItemStack.EMPTY));
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider holderProvider) {
        super.saveAdditional(tag, holderProvider);
        if (!trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, compost, holderProvider);
        }

        ListTag listTag = new ListTag();
        for (int i = 0; i < compostables.size(); ++i) {
            ItemStack itemStack = compostables.get(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Compostables", (byte) i);
                listTag.add(itemStack.save(holderProvider, compoundTag));
            }
        }

        tag.put("Compostables", listTag);
    }

    @Override
    public ItemStack getItem(int slot) {
        return compost.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(compost, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(compost, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        compost.set(slot, stack);
        if (slot == 27) {
            stack.setCount(1);
            setChanged();
        } else if (stack.getCount() > 64) {
            stack.setCount(64);
        }
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return compost;
    }

    @Override
    public void setItems(NonNullList<ItemStack> items) {
        this.compost = items;
    }

    @Override
    public void clearContent() {
        compost.clear();
    }

    @Override
    public int getContainerSize() {
        return compost.size();
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
            state = ContainerComposterBlock.addItem(null, getBlockState(), getLevel(), getBlockPos(), input);
            getLevel().levelEvent(LevelEvent.COMPOSTER_FILL, getBlockPos(), state != getBlockState() ? 1 : 0);
            removeItemNoUpdate(27);
        }
        if (state.getValue(ContainerComposterBlock.LEVEL) == 8 && isEmpty() && getLevel() != null) {
            ContainerComposterBlock.empty(null, getBlockState(), getLevel(), getBlockPos());
        }
        super.setChanged();
    }

}