package dev.yurisuika.compost.block;

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

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.IntStream;

import static dev.yurisuika.compost.Compost.*;
import static dev.yurisuika.compost.block.ComposterBlock.*;

public class ComposterBlockEntity extends LootableContainerBlockEntity implements SidedInventory {
    private static final int[] OUTPUT_SLOTS = IntStream.range(0, OUTPUT_SIZE).toArray();
    private static final int[] INPUT_SLOTS = new int[]{OUTPUT_SIZE};

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(OUTPUT_SIZE + 1, ItemStack.EMPTY);

    public ComposterBlockEntity(BlockPos pos, BlockState state) {
        super(COMPOSTER, pos, state);
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
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public int size() {
        return OUTPUT_SIZE + 1;
    }

    @Override
    public boolean isEmpty() {
        Iterator<ItemStack> invIterator = this.inventory.iterator();

        ItemStack stack;
        do {
            if (!invIterator.hasNext()) {
                return true;
            }

            stack = invIterator.next();
        } while (stack.isEmpty());

        return false;
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
    public void setStack(int index, ItemStack stack) {
        this.inventory.set(index, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
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
        return 1;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return side == Direction.DOWN ? OUTPUT_SLOTS : INPUT_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return dir != Direction.DOWN && this.getCachedState().get(ComposterBlock.LEVEL) < 7 &&
                slot == OUTPUT_SIZE && this.getStack(OUTPUT_SIZE).isEmpty() &&
                ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(stack.getItem());
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot < OUTPUT_SIZE && !stack.isEmpty();
    }

    @Override
    public void markDirty() {
        BlockState state = Objects.requireNonNull(this.getCachedState());
        ItemStack input = this.getStack(OUTPUT_SIZE);

        if (!input.isEmpty()) {
            state = ComposterBlockInvoker.invokeAddToComposter(null, state, this.world, this.pos, input);
            this.world.syncWorldEvent(1500, this.pos, state != this.getCachedState() ? 1 : 0);
            this.removeStack(OUTPUT_SIZE);
        }

        if (state.get(ComposterBlock.LEVEL) == 8 && this.isEmpty()) {
            emptyComposter(null, state, Objects.requireNonNull(this.world), this.pos);
        }

        super.markDirty();
    }
}