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
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldEvents;

import java.util.Objects;
import java.util.stream.IntStream;

public class ComposterBlockEntity extends LootableContainerBlockEntity implements SidedInventory {

    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27 + 1, ItemStack.EMPTY);

    public ComposterBlockEntity(BlockPos pos, BlockState state) {
        super(Compost.COMPOSTER, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY);
        if (!writeLootTable(nbt)) {
            Inventories.readNbt(nbt, inventory, registryLookup);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!readLootTable(nbt)) {
            Inventories.writeNbt(nbt, inventory, registryLookup);
        }
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
    public DefaultedList<ItemStack> getHeldStacks() {
        return inventory;
    }

    @Override
    public void setHeldStacks(DefaultedList<ItemStack> list) {
        inventory = list;
    }

    @Override
    public Text getContainerName() {
        return Text.translatable("container.compost.composter");
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
            state = ComposterBlockInvoker.invokeAddToComposter(null, state, world, pos, input);
            world.syncWorldEvent(WorldEvents.COMPOSTER_USED, pos, state != getCachedState() ? 1 : 0);
            removeStack(27);
        }
        if (state.get(ComposterBlock.LEVEL) == 8 && isEmpty()) {
            ComposterBlock.emptyComposter(null, state, Objects.requireNonNull(world), pos);
        }
        super.markDirty();
    }

}