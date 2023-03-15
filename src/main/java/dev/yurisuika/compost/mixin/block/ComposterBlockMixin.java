package dev.yurisuika.compost.mixin.block;

import com.google.common.collect.Lists;
import dev.yurisuika.compost.Compost;
import dev.yurisuika.compost.block.ArrayComposterInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static dev.yurisuika.compost.Compost.*;

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin {

    @Redirect(method = "emptyFullComposter", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private static boolean redirectEmptyFullComposter(World world, Entity entity) {
        return false;
    }

    @Inject(method = "emptyFullComposter", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", shift = At.Shift.AFTER))
    private static void injectEmptyFullComposter(Entity user, BlockState state, World world, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        Arrays.stream(Compost.config.items).forEach(group -> {
            if(ThreadLocalRandom.current().nextDouble() < group.chance) {
                double x = (double)(world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                double y = (double)(world.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
                double z = (double)(world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + x, (double)pos.getY() + y, (double)pos.getZ() + z, createItemStack(group));
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        });
    }

    @Inject(method = "getInventory", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void injectGetInventory(BlockState state, WorldAccess world, BlockPos pos, CallbackInfoReturnable<SidedInventory> cir) {
        List<ItemStack> list = Lists.newArrayList();
        Arrays.stream(Compost.config.items).forEach(group -> {
            if(ThreadLocalRandom.current().nextDouble() < group.chance) {
                list.add(createItemStack(group));
            }
        });
        if (Compost.config.shuffle) {
            Collections.shuffle(list);
        }
        ItemStack[] itemStacks = new ItemStack[list.size()];
        for (int i = 0; i < list.size(); i++) {
            itemStacks[i] = list.get(i);
        }
        cir.setReturnValue(new ArrayComposterInventory(state, world, pos, itemStacks));
    }

}