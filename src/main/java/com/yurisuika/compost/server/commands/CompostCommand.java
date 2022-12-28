package com.yurisuika.compost.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static com.yurisuika.compost.Compost.*;
import static net.minecraft.commands.Commands.*;

public class CompostCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(literal("compost")
                .then(literal("config")
                        .requires(source -> source.hasPermission(4))
                        .then(literal("reload")
                                .executes(context -> {
                                    loadConfig();

                                    context.getSource().sendSuccess(Component.translatable("commands.compost.config.reload"), true);
                                    return 1;
                                })
                        )
                        .then(literal("reset")
                                .executes(context -> {
                                    int length = config.items.length;
                                    for (int i = 0; i < length; i++) {
                                        removeGroup(0);
                                    }

                                    addGroup("minecraft:dirt", 1.0D, 1,1);
                                    addGroup("minecraft:bone_meal", 1.0D, 1,1);
                                    setShuffle(true);
                                    context.getSource().sendSuccess(Component.translatable("commands.compost.config.reset"), true);
                                    return 1;
                                })
                        )
                )
                .then(literal("shuffle")
                        .requires(source -> source.hasPermission(4))
                        .then(literal("query")
                                .executes(context -> {
                                    context.getSource().sendSuccess(Component.translatable("commands.compost.shuffle.query", config.shuffle), false);
                                    return 1;
                                })
                        )
                        .then(literal("set")
                                .then(argument("value", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean value = BoolArgumentType.getBool(context, "value");

                                            setShuffle(value);
                                            context.getSource().sendSuccess(Component.translatable("commands.compost.shuffle.set", value), true);
                                            return 1;
                                        })
                                )
                        )
                )
                .then(literal("groups")
                        .requires(source -> source.hasPermission(4))
                        .then(literal("query")
                                .executes(context -> {
                                    context.getSource().sendSuccess(Component.translatable("commands.compost.groups.query", config.items.length), false);
                                    return 1;
                                })
                        )
                        .then(literal("get")
                                .executes(context -> {
                                    for (Group group : config.items) {
                                        int index;
                                        Item item;
                                        if (group.item.contains("{")) {
                                            index = group.item.indexOf("{");
                                            item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(group.item.substring(0, index)));
                                        } else {
                                            index = 0;
                                            item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(group.item));
                                        }
                                        ItemStack itemStack = new ItemStack(item);
                                        if (group.item.contains("{")) {
                                            CompoundTag nbt;
                                            try {
                                                nbt = TagParser.parseTag(group.item.substring(index));
                                                itemStack.setTag(nbt);
                                            } catch (CommandSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        double chance = Math.max(0.0D, Math.min(group.chance, 1.0D));
                                        int maxCount = itemStack.getMaxStackSize();
                                        int max = Math.min(group.max, maxCount);
                                        int min = Math.min(Math.min(group.min, maxCount), max);

                                        context.getSource().sendSuccess(Component.translatable("commands.compost.groups.get", ArrayUtils.indexOf(config.items, group) + 1, itemStack.getDisplayName(), new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max), false);
                                    }
                                    return 1;
                                })
                        )
                        .then(literal("reverse")
                                .executes(context -> {
                                    reverseGroups();

                                    context.getSource().sendSuccess(Component.translatable("commands.compost.groups.reverse"), true);
                                    return 1;
                                })
                        )
                        .then(literal("shuffle")
                                .executes(context -> {
                                    shuffleGroups();

                                    context.getSource().sendSuccess(Component.translatable("commands.compost.groups.shuffle"), true);
                                    return 1;
                                })
                        )
                )
                .then(literal("group")
                        .requires(source -> source.hasPermission(4))
                        .then(literal("get")
                                .then(argument("group", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            int range = IntegerArgumentType.getInteger(context, "group");
                                            if (range > config.items.length) {
                                                context.getSource().sendFailure(Component.translatable("commands.compost.group.failed", range, config.items.length));
                                                return 0;
                                            } else {
                                                Group group = getGroup(range - 1);
                                                int index;
                                                Item item;
                                                if (group.item.contains("{")) {
                                                    index = group.item.indexOf("{");
                                                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(group.item.substring(0, index)));
                                                } else {
                                                    index = 0;
                                                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(group.item));
                                                }
                                                ItemStack itemStack = new ItemStack(item);
                                                if (group.item.contains("{")) {
                                                    CompoundTag nbt;
                                                    try {
                                                        nbt = TagParser.parseTag(group.item.substring(index));
                                                        itemStack.setTag(nbt);
                                                    } catch (CommandSyntaxException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                double chance = Math.max(0.0D, Math.min(group.chance, 1.0D));
                                                int maxCount = itemStack.getMaxStackSize();
                                                int max = Math.min(group.max, maxCount);
                                                int min = Math.min(Math.min(group.min, maxCount), max);

                                                context.getSource().sendSuccess(Component.translatable("commands.compost.groups.get", ArrayUtils.indexOf(config.items, group) + 1, itemStack.getDisplayName(), new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max), false);
                                                return 1;
                                            }
                                        })
                                )
                        )
                        .then(literal("remove")
                                .then(argument("group", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            int range = IntegerArgumentType.getInteger(context, "group");
                                            if (range > config.items.length) {
                                                context.getSource().sendFailure(Component.translatable("commands.compost.group.failed", range, config.items.length));
                                                return 0;
                                            } else {
                                                int number = IntegerArgumentType.getInteger(context, "group") - 1;
                                                Group group = getGroup(number);
                                                int index;
                                                Item item;
                                                if (group.item.contains("{")) {
                                                    index = group.item.indexOf("{");
                                                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(group.item.substring(0, index)));
                                                } else {
                                                    index = 0;
                                                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(group.item));
                                                }
                                                ItemStack itemStack = new ItemStack(item);
                                                if (group.item.contains("{")) {
                                                    CompoundTag nbt;
                                                    try {
                                                        nbt = TagParser.parseTag(group.item.substring(index));
                                                        itemStack.setTag(nbt);
                                                    } catch (CommandSyntaxException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                double chance = Math.max(0.0D, Math.min(group.chance, 1.0D));
                                                int maxCount = itemStack.getMaxStackSize();
                                                int max = Math.min(group.max, maxCount);
                                                int min = Math.min(Math.min(group.min, maxCount), max);

                                                removeGroup(number);
                                                context.getSource().sendSuccess(Component.translatable("commands.compost.group.remove", itemStack.getDisplayName(), new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max), true);
                                                return 1;
                                            }
                                        })
                                )
                        )
                        .then(literal("add")
                                .then(argument("item", ItemArgument.item(buildContext))
                                        .then(argument("chance", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                .then(argument("min", IntegerArgumentType.integer(0, 64))
                                                        .then(argument("max", IntegerArgumentType.integer(1, 64))
                                                                .executes(context -> {
                                                                    ItemInput arg = ItemArgument.getItem(context, "item");
                                                                    ItemStack itemStack = arg.createItemStack(1, false);
                                                                    String item = arg.serialize();
                                                                    double chance = Math.max(0.0D, Math.min(DoubleArgumentType.getDouble(context, "chance"), 1.0D));
                                                                    int maxCount = itemStack.getMaxStackSize();
                                                                    int max = Math.min(IntegerArgumentType.getInteger(context, "max"), maxCount);
                                                                    int min = Math.min(Math.min(IntegerArgumentType.getInteger(context, "min"), maxCount), max);

                                                                    addGroup(item, chance, min, max);
                                                                    context.getSource().sendSuccess(Component.translatable("commands.compost.group.add", itemStack.getDisplayName(), new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max), true);
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(literal("insert")
                                .then(argument("group", IntegerArgumentType.integer(1))
                                        .then(argument("item", ItemArgument.item(buildContext))
                                                .then(argument("chance", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                        .then(argument("min", IntegerArgumentType.integer(0, 64))
                                                                .then(argument("max", IntegerArgumentType.integer(1, 64))
                                                                        .executes(context -> {
                                                                            int range = IntegerArgumentType.getInteger(context, "group");
                                                                            if (range > config.items.length) {
                                                                                context.getSource().sendFailure(Component.translatable("commands.compost.group.failed", range, config.items.length));
                                                                                return 0;
                                                                            } else {
                                                                                int number = IntegerArgumentType.getInteger(context, "group") - 1;
                                                                                ItemInput arg = ItemArgument.getItem(context, "item");
                                                                                ItemStack itemStack = arg.createItemStack(1, false);
                                                                                String item = arg.serialize();
                                                                                double chance = Math.max(0.0D, Math.min(DoubleArgumentType.getDouble(context, "chance"), 1.0D));
                                                                                int maxCount = itemStack.getMaxStackSize();
                                                                                int max = Math.min(IntegerArgumentType.getInteger(context, "max"), maxCount);
                                                                                int min = Math.min(Math.min(IntegerArgumentType.getInteger(context, "min"), maxCount), max);

                                                                                insertGroup(number, item, chance, min, max);
                                                                                context.getSource().sendSuccess(Component.translatable("commands.compost.group.insert", itemStack.getDisplayName(), new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max), true);
                                                                                return 1;
                                                                            }
                                                                        })
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(literal("set")
                                .then(argument("group", IntegerArgumentType.integer(1))
                                        .then(argument("item", ItemArgument.item(buildContext))
                                                .then(argument("chance", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                        .then(argument("min", IntegerArgumentType.integer(0, 64))
                                                                .then(argument("max", IntegerArgumentType.integer(1, 64))
                                                                        .executes(context -> {
                                                                            int range = IntegerArgumentType.getInteger(context, "group");
                                                                            if (range > config.items.length) {
                                                                                context.getSource().sendFailure(Component.translatable("commands.compost.group.failed", range, config.items.length));
                                                                                return 0;
                                                                            } else {
                                                                                int number = IntegerArgumentType.getInteger(context, "group") - 1;
                                                                                ItemInput arg = ItemArgument.getItem(context, "item");
                                                                                ItemStack itemStack = arg.createItemStack(1, false);
                                                                                String item = arg.serialize();
                                                                                double chance = Math.max(0.0D, Math.min(DoubleArgumentType.getDouble(context, "chance"), 1.0D));
                                                                                int maxCount = itemStack.getMaxStackSize();
                                                                                int max = Math.min(IntegerArgumentType.getInteger(context, "max"), maxCount);
                                                                                int min = Math.min(Math.min(IntegerArgumentType.getInteger(context, "min"), maxCount), max);

                                                                                setGroup(number, item, chance, min, max);
                                                                                context.getSource().sendSuccess(Component.translatable("commands.compost.group.set", itemStack.getDisplayName(), new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max), true);
                                                                                return 1;
                                                                            }
                                                                        })
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }



}