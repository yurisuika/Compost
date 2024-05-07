package dev.yurisuika.compost.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.yurisuika.compost.server.option.CompostConfig;
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
import java.util.concurrent.atomic.AtomicInteger;

import static dev.yurisuika.compost.server.option.CompostConfig.*;
import static dev.yurisuika.compost.util.ConfigUtil.*;
import static dev.yurisuika.compost.util.NetworkUtil.*;
import static net.minecraft.server.command.CommandManager.*;

public class CompostCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("compost")
                .then(literal("config")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("reload")
                                .executes(context -> {
                                    loadConfig();
                                    checkWorlds(context.getSource().getServer());
                                    for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                                        sendGroups(context.getSource().getWorld(), player);
                                    }
                                    context.getSource().sendFeedback(() -> Text.translatable("commands.compost.config.reload"), true);
                                    return 1;
                                })
                        )
                        .then(literal("reset")
                                .executes(context -> {
                                    config.worlds = new CompostConfig.Config.World[]{};
                                    config.worlds = ArrayUtils.add(config.worlds, new Config.World("world", new Config.World.Group[]{
                                            new Config.World.Group("minecraft:dirt", 1.0D, 1, 1),
                                            new Config.World.Group("minecraft:bone_meal", 1.0D, 1, 1)
                                    }));
                                    checkWorlds(context.getSource().getServer());
                                    for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                                        sendGroups(context.getSource().getWorld(), player);
                                    }
                                    context.getSource().sendFeedback(() -> Text.translatable("commands.compost.config.reset"), true);
                                    return 1;
                                })
                        )
                )
                .then(literal("groups")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("query")
                                .executes(context -> {
                                    Config.World world = config.worlds[getIndex(context.getSource().getServer().getSaveProperties().getLevelName()).get()];
                                    for (Config.World.Group group : world.items) {
                                        context.getSource().sendFeedback(() -> Text.translatable("commands.compost.groups.query", ArrayUtils.indexOf(world.items, group) + 1, new DecimalFormat("0.###############").format(BigDecimal.valueOf(group.chance).multiply(BigDecimal.valueOf(100))), (group.min != group.max ? group.min + "-" + group.max : String.valueOf(group.max)), createItemStack(group).toHoverableText()), false);
                                    }
                                    return 1;
                                })
                        )
                        .then(literal("add")
                                .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                        .then(argument("chance", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                .then(argument("min", IntegerArgumentType.integer(0, 64))
                                                        .then(argument("max", IntegerArgumentType.integer(1, 64))
                                                                .executes(context -> {
                                                                    ItemStackArgument arg = ItemStackArgumentType.getItemStackArgument(context, "item");
                                                                    ItemStack itemStack = arg.createStack(1, false);
                                                                    double chance = Math.max(0.0D, Math.min(DoubleArgumentType.getDouble(context, "chance"), 1.0D));
                                                                    int min = Math.min(Math.min(IntegerArgumentType.getInteger(context, "min"), itemStack.getMaxCount()), IntegerArgumentType.getInteger(context, "max"));
                                                                    int max = Math.max(Math.min(IntegerArgumentType.getInteger(context, "max"), itemStack.getMaxCount()), IntegerArgumentType.getInteger(context, "min"));
                                                                    addGroup(config.worlds[getIndex(context.getSource().getServer().getSaveProperties().getLevelName()).get()].world, arg.asString(context.getSource().getWorld().getRegistryManager()), chance, min, max);
                                                                    for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                                                                        sendGroups(context.getSource().getWorld(), player);
                                                                    }
                                                                    context.getSource().sendFeedback(() -> Text.translatable("commands.compost.groups.add", new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), (min != max ? min + "-" + max : String.valueOf(max)), itemStack.toHoverableText()), true);
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(literal("remove")
                                .then(argument("group", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            AtomicInteger index = getIndex(context.getSource().getServer().getSaveProperties().getLevelName());
                                            Config.World world = config.worlds[index.get()];
                                            int range = IntegerArgumentType.getInteger(context, "group");
                                            if (range > config.worlds[index.get()].items.length) {
                                                context.getSource().sendError(Text.translatable("commands.compost.groups.remove.failed", range, world.items.length));
                                                return 0;
                                            } else {
                                                int number = IntegerArgumentType.getInteger(context, "group") - 1;
                                                Text item = createItemStack(getGroup(world.world, number)).toHoverableText();
                                                removeGroup(world.world, number);
                                                for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                                                    sendGroups(context.getSource().getWorld(), player);
                                                }
                                                context.getSource().sendFeedback(() -> Text.translatable("commands.compost.groups.remove", item), true);
                                                return 1;
                                            }
                                        })
                                )
                        )
                )
        );
    }

}