package com.yurisuika.compost.mixin.block;

import com.google.common.collect.Lists;
import com.yurisuika.compost.Compost;
import com.yurisuika.compost.block.ArrayComposterInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
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

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin {

    @Redirect(method = "emptyFullComposter", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private static boolean redirectEmptyFullComposter(World world, Entity entity) {
        return false;
    }

    @Inject(method = "emptyFullComposter", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", shift = At.Shift.AFTER))
    private static void injectEmptyFullComposter(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        Arrays.stream(Compost.config.items).forEach(group -> {
            double d = (double)(world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
            double e = (double)(world.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
            double g = (double)(world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
            ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, new ItemStack(Registries.ITEM.get(new Identifier(group.item)), ThreadLocalRandom.current().nextInt(group.min, group.max + 1)));
            itemEntity.setToDefaultPickupDelay();
            if(ThreadLocalRandom.current().nextFloat() < group.chance) {
                world.spawnEntity(itemEntity);
            }
        });
    }

    @Inject(method = "getInventory", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void injectGetInventory(BlockState state, WorldAccess world, BlockPos pos, CallbackInfoReturnable<SidedInventory> cir) {
        List<ItemStack> list = Lists.newArrayList();
        Arrays.stream(Compost.config.items).forEach(group -> {
            if(ThreadLocalRandom.current().nextFloat() < group.chance) {
                Item output = Registries.ITEM.get(new Identifier(group.item));
                int count = ThreadLocalRandom.current().nextInt(group.min, group.max + 1);
                list.add(new ItemStack(output, count));
            }
        });

        if (Compost.config.shuffle) {
            Collections.shuffle(list);
        }

        ItemStack[] itemStacks = new ItemStack[list.size()];
        for (int m = 0; m < list.size(); m++) {
            itemStacks[m] = list.get(m);
        }

        cir.setReturnValue(new ArrayComposterInventory(state, world, pos, itemStacks));
    }

}