package com.yurisuika.compost.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static com.yurisuika.compost.Compost.*;
import static net.minecraft.server.command.CommandManager.*;

public class CompostCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("compost")
                .then(literal("config")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("reload")
                                .executes(context -> {
                                    loadConfig();
                                    context.getSource().sendFeedback(Text.translatable("commands.compost.config.reload"), true);
                                    return 1;
                                })
                        )
                        .then(literal("reset")
                                .executes(context -> {
                                    for (int i = 0; i < config.items.length; i++) {
                                        removeGroup(i);
                                    }
                                    addGroup("minecraft:dirt", 1.0D, 1,1);
                                    addGroup("minecraft:bone_meal", 1.0D, 1,1);
                                    setShuffle(true);
                                    context.getSource().sendFeedback(Text.translatable("commands.compost.config.reset"), true);
                                    return 1;
                                })
                        )
                )
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
                                        int index;
                                        String name;
                                        String id;
                                        if (group.item.contains("{")) {
                                            index = group.item.indexOf("{");
                                            name = Registries.ITEM.get(new Identifier(group.item.substring(0, group.item.indexOf("{")))).getName().getString();
                                            id = group.item.substring(0, index);
                                        } else {
                                            name = Registries.ITEM.get(new Identifier(group.item)).getName().getString();
                                            id = group.item;
                                        }
                                        int limit = Registries.ITEM.get(new Identifier(id)).getMaxCount();
                                        int min = Math.min(group.min, limit);
                                        int max = Math.min(group.max, limit);
                                        context.getSource().sendFeedback(Text.translatable("commands.compost.groups.get", ArrayUtils.indexOf(config.items, group), new DecimalFormat("0.###############").format(BigDecimal.valueOf(group.chance).multiply(BigDecimal.valueOf(100))), min, max, name), false);
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
                                            Group group = getGroup(IntegerArgumentType.getInteger(context, "group"));
                                            int index;
                                            String item;
                                            String id;
                                            if (group.item.contains("{")) {
                                                index = group.item.indexOf("{");
                                                item = Registries.ITEM.get(new Identifier(group.item.substring(0, group.item.indexOf("{")))).getName().getString();
                                                id = group.item.substring(0, index);
                                            } else {
                                                item = Registries.ITEM.get(new Identifier(group.item)).getName().getString();
                                                id = group.item;
                                            }
                                            int limit = Registries.ITEM.get(new Identifier(id)).getMaxCount();
                                            int min = Math.min(group.min, limit);
                                            int max = Math.min(group.max, limit);
                                            context.getSource().sendFeedback(Text.translatable("commands.compost.groups.get", ArrayUtils.indexOf(config.items, group), new DecimalFormat("0.###############").format(BigDecimal.valueOf(group.chance).multiply(BigDecimal.valueOf(100))), min, max, item), false);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("remove")
                                .then(argument("group", IntegerArgumentType.integer(0, config.items.length - 1))
                                        .executes(context -> {
                                            int number = IntegerArgumentType.getInteger(context, "group");
                                            removeGroup(number);
                                            context.getSource().sendFeedback(Text.translatable("commands.compost.group.remove", number), true);
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
                                                                    String name;
                                                                    String id;
                                                                    int index;
                                                                    if (item.contains("{")) {
                                                                        index = item.indexOf("{");
                                                                        name = Registries.ITEM.get(new Identifier(item.substring(0, index))).getName().getString();
                                                                        id = item.substring(0, index);
                                                                    } else {
                                                                        name = Registries.ITEM.get(new Identifier(item)).getName().getString();
                                                                        id = item;
                                                                    }
                                                                    double chance = DoubleArgumentType.getDouble(context, "chance");
                                                                    int limit = Registries.ITEM.get(new Identifier(id)).getMaxCount();
                                                                    int min = Math.min(IntegerArgumentType.getInteger(context, "min"), limit);
                                                                    int max = Math.min(IntegerArgumentType.getInteger(context, "max"), limit);
                                                                    addGroup(item, chance, min, max);
                                                                    context.getSource().sendFeedback(Text.translatable("commands.compost.group.add", new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max, name), true);
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
                                                                            int number = IntegerArgumentType.getInteger(context, "group");
                                                                            String item = ItemStackArgumentType.getItemStackArgument(context, "item").asString();
                                                                            String name;
                                                                            String id;
                                                                            int index;
                                                                            if (item.contains("{")) {
                                                                                index = item.indexOf("{");
                                                                                name = Registries.ITEM.get(new Identifier(item.substring(0, index))).getName().getString();
                                                                                id = item.substring(0, index);
                                                                            } else {
                                                                                name = Registries.ITEM.get(new Identifier(item)).getName().getString();
                                                                                id = item;
                                                                            }
                                                                            double chance = DoubleArgumentType.getDouble(context, "chance");
                                                                            int limit = Registries.ITEM.get(new Identifier(id)).getMaxCount();
                                                                            int min = Math.min(IntegerArgumentType.getInteger(context, "min"), limit);
                                                                            int max = Math.min(IntegerArgumentType.getInteger(context, "max"), limit);
                                                                            insertGroup(number, item, chance, min, max);
                                                                            context.getSource().sendFeedback(Text.translatable("commands.compost.group.insert", number, new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max, name), true);
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
                                                                            int number = IntegerArgumentType.getInteger(context, "group");
                                                                            String item = ItemStackArgumentType.getItemStackArgument(context, "item").asString();
                                                                            String name;
                                                                            String id;
                                                                            int index;
                                                                            if (item.contains("{")) {
                                                                                index = item.indexOf("{");
                                                                                name = Registries.ITEM.get(new Identifier(item.substring(0, index))).getName().getString();
                                                                                id = item.substring(0, index);
                                                                            } else {
                                                                                name = Registries.ITEM.get(new Identifier(item)).getName().getString();
                                                                                id = item;
                                                                            }
                                                                            double chance = DoubleArgumentType.getDouble(context, "chance");
                                                                            int limit = Registries.ITEM.get(new Identifier(id)).getMaxCount();
                                                                            int min = Math.min(IntegerArgumentType.getInteger(context, "min"), limit);
                                                                            int max = Math.min(IntegerArgumentType.getInteger(context, "max"), limit);
                                                                            setGroup(number, item, chance, min, max);
                                                                            context.getSource().sendFeedback(Text.translatable("commands.compost.group.set", number, new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max, name), true);
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
