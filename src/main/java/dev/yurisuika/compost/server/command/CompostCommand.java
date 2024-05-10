package dev.yurisuika.compost.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.yurisuika.compost.util.ConfigUtil;
import dev.yurisuika.compost.util.CompostUtil;
import dev.yurisuika.compost.util.NetworkUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static dev.yurisuika.compost.config.CompostConfig.*;
import static net.minecraft.server.command.CommandManager.*;

public class CompostCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("compost")
                .then(literal("config")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("reload")
                                .executes(context -> {
                                    ConfigUtil.loadConfig();
                                    CompostUtil.checkLevels(context.getSource().getServer());
                                    for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                                        NetworkUtil.sendItems(context.getSource().getWorld(), player);
                                    }
                                    context.getSource().sendFeedback(() -> Text.translatable("commands.compost.config.reload"), true);
                                    return 1;
                                })
                        )
                        .then(literal("reset")
                                .executes(context -> {
                                    config.levels = new Config.Level[]{};
                                    config.levels = ArrayUtils.add(config.levels, new Config.Level("world", new Config.Level.Item[]{
                                            new Config.Level.Item("minecraft:dirt", 1.0D, 1, 1),
                                            new Config.Level.Item("minecraft:bone_meal", 1.0D, 1, 1)
                                    }));
                                    CompostUtil.checkLevels(context.getSource().getServer());
                                    for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                                        NetworkUtil.sendItems(context.getSource().getWorld(), player);
                                    }
                                    context.getSource().sendFeedback(() -> Text.translatable("commands.compost.config.reset"), true);
                                    return 1;
                                })
                        )
                )
                .then(literal("items")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("query")
                                .executes(context -> {
                                    Config.Level level = CompostUtil.getLevel(context.getSource().getServer().getSaveProperties().getLevelName());
                                    for (Config.Level.Item item : level.items) {
                                        context.getSource().sendFeedback(() -> Text.translatable("commands.compost.items.query", ArrayUtils.indexOf(level.items, item) + 1, new DecimalFormat("0.###############").format(BigDecimal.valueOf(item.chance).multiply(BigDecimal.valueOf(100))), (item.min != item.max ? item.min + "-" + item.max : String.valueOf(item.max)), CompostUtil.createItemStack(item).toHoverableText()), false);
                                    }
                                    return 1;
                                })
                        )
                        .then(literal("add")
                                .then(argument("names", ItemStackArgumentType.itemStack(registryAccess))
                                        .then(argument("chances", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                .then(argument("mins", IntegerArgumentType.integer(0, 64))
                                                        .then(argument("maxes", IntegerArgumentType.integer(1, 64))
                                                                .executes(context -> {
                                                                    ItemStackArgument arg = ItemStackArgumentType.getItemStackArgument(context, "names");
                                                                    ItemStack itemStack = arg.createStack(1, false);
                                                                    double chance = Math.max(0.0D, Math.min(DoubleArgumentType.getDouble(context, "chances"), 1.0D));
                                                                    int min = Math.min(Math.min(IntegerArgumentType.getInteger(context, "mins"), itemStack.getMaxCount()), IntegerArgumentType.getInteger(context, "maxes"));
                                                                    int max = Math.max(Math.min(IntegerArgumentType.getInteger(context, "maxes"), itemStack.getMaxCount()), IntegerArgumentType.getInteger(context, "mins"));
                                                                    CompostUtil.addItem(CompostUtil.getLevel(context.getSource().getServer().getSaveProperties().getLevelName()).name, new Config.Level.Item(arg.asString(context.getSource().getWorld().getRegistryManager()), chance, min, max));
                                                                    for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                                                                        NetworkUtil.sendItems(context.getSource().getWorld(), player);
                                                                    }
                                                                    context.getSource().sendFeedback(() -> Text.translatable("commands.compost.items.add", new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), (min != max ? min + "-" + max : String.valueOf(max)), itemStack.toHoverableText()), true);
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(literal("remove")
                                .then(argument("index", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            Config.Level level = CompostUtil.getLevel(context.getSource().getServer().getSaveProperties().getLevelName());
                                            int index = IntegerArgumentType.getInteger(context, "index");
                                            if (index > level.items.length) {
                                                context.getSource().sendError(Text.translatable("commands.compost.items.remove.failed", index, level.items.length));
                                                return 0;
                                            } else {
                                                Text item = CompostUtil.createItemStack(CompostUtil.getLevel(level.name).items[index - 1]).toHoverableText();
                                                CompostUtil.removeItem(level.name, index - 1);
                                                for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                                                    NetworkUtil.sendItems(context.getSource().getWorld(), player);
                                                }
                                                context.getSource().sendFeedback(() -> Text.translatable("commands.compost.items.remove", item), true);
                                                return 1;
                                            }
                                        })
                                )
                        )
                )
        );
    }

}