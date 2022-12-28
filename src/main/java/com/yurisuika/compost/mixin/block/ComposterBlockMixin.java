package com.yurisuika.compost.mixin.block;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.yurisuika.compost.Compost;
import com.yurisuika.compost.block.ArrayFullInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
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
public class ComposterBlockMixin {

    @Redirect(method = "extractProduce", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addFreshEntity(Lnet/minecraft/entity/Entity;)Z"))
    private static boolean redirectExtractProduce(World world, Entity entity) {
        return false;
    }

    @Inject(method = "extractProduce", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addFreshEntity(Lnet/minecraft/entity/Entity;)Z", shift = At.Shift.AFTER))
    private static void injectExtractProduce(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        Arrays.stream(Compost.config.items).forEach(group -> {
            if(ThreadLocalRandom.current().nextDouble() < group.chance) {
                double x = (double)(world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                double y = (double)(world.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
                double z = (double)(world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                Item item;
                int index;
                if (group.item.contains("{")) {
                    index = group.item.indexOf("{");
                    String id = group.item.substring(0, index);
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
                } else {
                    index = 0;
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(group.item));
                }
                int maxCount = item.getMaxStackSize();
                int max = Math.min(group.max, maxCount);
                int min = Math.min(Math.min(group.min, maxCount), max);
                ItemStack itemStack = new ItemStack(item, ThreadLocalRandom.current().nextInt(min, max + 1));
                if (group.item.contains("{")) {
                    CompoundNBT nbt;
                    try {
                        nbt = JsonToNBT.parseTag(group.item.substring(index));
                        itemStack.setTag(nbt);
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                }
                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + x, (double)pos.getY() + y, (double)pos.getZ() + z, itemStack);
                itemEntity.setDefaultPickUpDelay();
                world.addFreshEntity(itemEntity);
            }
        });
    }

    @Inject(method = "getContainer", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void injectGetContainer(BlockState state, IWorld level, BlockPos pos, CallbackInfoReturnable<ISidedInventory> cir) {
        List<ItemStack> list = Lists.newArrayList();
        Arrays.stream(Compost.config.items).forEach(group -> {
            if(ThreadLocalRandom.current().nextDouble() < group.chance) {
                Item item;
                int index;
                if (group.item.contains("{")) {
                    index = group.item.indexOf("{");
                    String id = group.item.substring(0, index);
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
                } else {
                    index = 0;
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(group.item));
                }
                int maxCount = item.getMaxStackSize();
                int max = Math.min(group.max, maxCount);
                int min = Math.min(Math.min(group.min, maxCount), max);
                ItemStack itemStack = new ItemStack(item, ThreadLocalRandom.current().nextInt(min, max + 1));
                if (group.item.contains("{")) {
                    CompoundNBT nbt;
                    try {
                        nbt = JsonToNBT.parseTag(group.item.substring(index));
                        itemStack.setTag(nbt);
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                }
                list.add(itemStack);
            }
        });

        if (Compost.config.shuffle) {
            Collections.shuffle(list);
        }

        ItemStack[] itemStacks = new ItemStack[list.size()];
        for (int m = 0; m < list.size(); m++) {
            itemStacks[m] = list.get(m);
        }

        cir.setReturnValue(new ArrayFullInventory(state, level, pos, itemStacks));
    }

}