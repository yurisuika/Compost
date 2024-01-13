package dev.yurisuika.compost.block.entity;

import dev.yurisuika.compost.block.ComposterBlock;
import dev.yurisuika.compost.mixin.block.ComposterBlockInvoker;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Objects;
import java.util.stream.IntStream;

import static dev.yurisuika.compost.Compost.*;
import static dev.yurisuika.compost.block.ComposterBlock.*;

public class ComposterBlockEntity extends LootableContainerBlockEntity implements SidedInventory {

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27 + 1, ItemStack.EMPTY);

    public ComposterBlockEntity(BlockPos pos, BlockState state) {
        super(COMPOSTER.get(), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory, false);
        }
    }

    @Override
    public ItemStack getStack(int index) {
        return this.inventory.get(index);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            if (slot == 27) {
                stack.setCount(1);
            } else {
                stack.setCount(this.getMaxCountPerStack());
            }
        }
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public int size() {
        return 27 + 1;
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.compost.composter");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return side == Direction.DOWN ? IntStream.range(0, 27).toArray() : new int[]{27};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.UP && this.getCachedState().get(ComposterBlock.LEVEL) < 7 && slot == 27 && this.getStack(27).isEmpty() && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(stack.getItem());
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot < 27 && !stack.isEmpty();
    }

    @Override
    public void markDirty() {
        BlockState state = Objects.requireNonNull(this.getCachedState());
        ItemStack input = this.getStack(27);
        if (!input.isEmpty() && state.get(ComposterBlock.LEVEL) < 7) {
            state = ComposterBlockInvoker.invokeAddToComposter(state, this.world, this.pos, input);
            this.world.syncWorldEvent(1500, this.pos, state != this.getCachedState() ? 1 : 0);
            this.removeStack(27);
        }
        if (state.get(ComposterBlock.LEVEL) == 8 && this.isEmpty()) {
            emptyComposter(state, Objects.requireNonNull(this.world), this.pos);
        }
        super.markDirty();
    }

}