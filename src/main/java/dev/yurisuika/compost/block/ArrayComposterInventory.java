package dev.yurisuika.compost.block;

import dev.yurisuika.compost.mixin.block.ComposterBlockInvoker;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ArrayComposterInventory extends SimpleInventory implements SidedInventory {

    private final BlockState state;
    private final WorldAccess world;
    private final BlockPos pos;
    private boolean dirty;

    public ArrayComposterInventory(BlockState state, WorldAccess world, BlockPos pos, ItemStack[] outputItems) {
        super(outputItems);
        this.state = state;
        this.world = world;
        this.pos = pos;
    }

    public int getMaxCountPerStack() {
        return 64;
    }

    public int[] getAvailableSlots(Direction side) {
        return side == Direction.DOWN ? new int[]{0} : new int[0];
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return !this.dirty && dir == Direction.DOWN && !stack.isEmpty();
    }

    public void markDirty() {
        ComposterBlockInvoker.invokeEmptyComposter(this.state, this.world, this.pos);
        this.dirty = true;
    }

}