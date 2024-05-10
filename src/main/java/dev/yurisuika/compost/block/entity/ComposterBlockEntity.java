package dev.yurisuika.compost.block.entity;

import dev.yurisuika.compost.Compost;
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.Objects;
import java.util.stream.IntStream;

public class ComposterBlockEntity extends LootableContainerBlockEntity implements SidedInventory {

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27 + 1, ItemStack.EMPTY);

    public ComposterBlockEntity() {
        super(Compost.COMPOSTER.get());
    }

    @Override
    public void fromTag(BlockState state, NbtCompound nbt) {
        super.fromTag(state, nbt);
        inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY);
        if (!deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, inventory);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, inventory, false);
        }
        return nbt;
    }

    @Override
    public ItemStack getStack(int index) {
        return inventory.get(index);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            if (slot == 27) {
                stack.setCount(1);
            } else {
                stack.setCount(getMaxCountPerStack());
            }
        }
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public DefaultedList<ItemStack> getInvStackList() {
        return inventory;
    }

    @Override
    public void setInvStackList(DefaultedList<ItemStack> list) {
        inventory = list;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.compost.composter");
    }

    @Override
    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
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
    public boolean isValid(int slot, ItemStack stack) {
        return getCachedState().get(ComposterBlock.LEVEL) < 7 && slot == 27 && getStack(27).isEmpty() && ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(stack.getItem());
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.UP && getCachedState().get(ComposterBlock.LEVEL) < 7 && slot == 27 && getStack(27).isEmpty() && ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(stack.getItem());
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot < 27 && !stack.isEmpty();
    }

    @Override
    public void markDirty() {
        BlockState state = Objects.requireNonNull(getCachedState());
        ItemStack input = getStack(27);
        if (!input.isEmpty() && state.get(ComposterBlock.LEVEL) < 7) {
            state = ComposterBlockInvoker.invokeAddToComposter(state, world, pos, input);
            world.syncWorldEvent(1500, pos, state != getCachedState() ? 1 : 0);
            removeStack(27);
        }
        if (state.get(ComposterBlock.LEVEL) == 8 && isEmpty()) {
            ComposterBlock.emptyComposter(state, Objects.requireNonNull(world), pos);
        }
        super.markDirty();
    }

}