package com.yurisuika.compost.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static com.yurisuika.compost.Compost.*;
import static net.minecraft.server.command.CommandManager.*;

public class CompostCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("compost")
                .then(literal("shuffle")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("query")
                                .executes(context -> {
                                    context.getSource().sendFeedback(Text.translatable("commands.compost.shuffle.query", config.shuffle), false);
                                    return 1;
                                })
                        )
                        .then(literal("set")
                                .then(argument("value", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean value = BoolArgumentType.getBool(context, "value");
                                            setShuffle(value);
                                            context.getSource().sendFeedback(Text.translatable("commands.compost.shuffle.set", value), true);
                                            return 1;
                                        })
                                )
                        )
                )
                .then(literal("groups")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("query")
                                .executes(context -> {
                                    context.getSource().sendFeedback(Text.translatable("commands.compost.groups.query", config.items.length), false);
                                    return 1;
                                })
                        )
                        .then(literal("get")
                                .executes(context -> {
                                    for (Group group : config.items) {
                                        context.getSource().sendFeedback(Text.translatable("commands.compost.groups.get", ArrayUtils.indexOf(config.items, group), new DecimalFormat("0.###############").format(BigDecimal.valueOf(group.chance).multiply(BigDecimal.valueOf(100))), group.min, group.max, group.item), false);
                                    }
                                    return 1;
                                })
                        )
                        .then(literal("reverse")
                                .executes(context -> {
                                    reverseGroups();
                                    context.getSource().sendFeedback(Text.translatable("commands.compost.groups.reverse"), true);
                                    return 1;
                                })
                        )
                        .then(literal("shuffle")
                                .executes(context -> {
                                    shuffleGroups();
                                    context.getSource().sendFeedback(Text.translatable("commands.compost.groups.shuffle"), true);
                                    return 1;
                                })
                        )
                )
                .then(literal("group")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("get")
                                .then(argument("group", IntegerArgumentType.integer(0, config.items.length - 1))
                                        .executes(context -> {
                                            int group = IntegerArgumentType.getInteger(context, "group");
                                            context.getSource().sendFeedback(Text.translatable("commands.compost.group.get", group, new DecimalFormat("0.###############").format(BigDecimal.valueOf(getGroup(group).chance).multiply(BigDecimal.valueOf(100))), getGroup(group).min, getGroup(group).max, getGroup(group).item), false);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("remove")
                                .then(argument("group", IntegerArgumentType.integer(0, config.items.length - 1))
                                        .executes(context -> {
                                            int group = IntegerArgumentType.getInteger(context, "group");
                                            removeGroup(group);
                                            context.getSource().sendFeedback(Text.translatable("commands.compost.group.remove", group), true);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("add")
                                .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                        .then(argument("chance", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                .then(argument("min", IntegerArgumentType.integer(0, 64))
                                                        .then(argument("max", IntegerArgumentType.integer(1, 64))
                                                                .executes(context -> {
                                                                    String item = ItemStackArgumentType.getItemStackArgument(context, "item").asString();
                                                                    double chance = DoubleArgumentType.getDouble(context, "chance");
                                                                    int min = IntegerArgumentType.getInteger(context, "min");
                                                                    int max = IntegerArgumentType.getInteger(context, "max");
                                                                    addGroup(item, chance, min, max);
                                                                    String itemx = ItemStackArgumentType.getItemStackArgument(context, "item").getItem().getName().getString();
                                                                    context.getSource().sendFeedback(Text.translatable("commands.compost.group.add", new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max, itemx), true);
                                                                    LOGGER.info(item);
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(literal("insert")
                                .then(argument("group", IntegerArgumentType.integer(0, config.items.length - 1))
                                        .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                                .then(argument("chance", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                        .then(argument("min", IntegerArgumentType.integer(0, 64))
                                                                .then(argument("max", IntegerArgumentType.integer(1, 64))
                                                                        .executes(context -> {
                                                                            int group = IntegerArgumentType.getInteger(context, "group");
                                                                            String item = ItemStackArgumentType.getItemStackArgument(context, "item").asString();
                                                                            double chance = DoubleArgumentType.getDouble(context, "chance");
                                                                            int min = IntegerArgumentType.getInteger(context, "min");
                                                                            int max = IntegerArgumentType.getInteger(context, "max");
                                                                            insertGroup(group, item, chance, min, max);
                                                                            context.getSource().sendFeedback(Text.translatable("commands.compost.group.insert", group, new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max, item), true);
                                                                            return 1;
                                                                        })
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(literal("set")
                                .then(argument("group", IntegerArgumentType.integer(0, config.items.length - 1))
                                        .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                                .then(argument("chance", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                        .then(argument("min", IntegerArgumentType.integer(0, 64))
                                                                .then(argument("max", IntegerArgumentType.integer(1, 64))
                                                                        .executes(context -> {
                                                                            int group = IntegerArgumentType.getInteger(context, "group");
                                                                            String item = ItemStackArgumentType.getItemStackArgument(context, "item").asString();
                                                                            double chance = DoubleArgumentType.getDouble(context, "chance");
                                                                            int min = IntegerArgumentType.getInteger(context, "min");
                                                                            int max = IntegerArgumentType.getInteger(context, "max");
                                                                            setGroup(group, item, chance, min, max);
                                                                            context.getSource().sendFeedback(Text.translatable("commands.compost.group.set", group, new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max, item), true);
                                                                            return 1;
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
