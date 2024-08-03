package dev.yurisuika.compost.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.yurisuika.compost.commands.arguments.ProduceArgument;
import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import dev.yurisuika.compost.util.Validate;
import dev.yurisuika.compost.util.config.Config;
import dev.yurisuika.compost.util.config.Option;
import dev.yurisuika.compost.util.config.options.Produce;
import dev.yurisuika.compost.util.config.options.World;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CompostCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, CommandSelection selection) {
        dispatcher.register(Commands.literal("compost")
                .then(Commands.literal("config")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.literal("reload")
                                .executes(commandContext -> {
                                    Config.loadConfig();
                                    Validate.checkLevels(commandContext.getSource().getServer());
                                    for (ServerPlayer player : commandContext.getSource().getServer().getPlayerList().getPlayers()) {
                                        Network.sendProduce(commandContext.getSource().getLevel(), player);
                                    }
                                    commandContext.getSource().sendSuccess(() -> Component.translatable("commands.compost.config.reload"), true);
                                    return 1;
                                })
                        )
                        .then(Commands.literal("reset")
                                .executes(commandContext -> {
                                    Option.setWorlds(new ArrayList<>());
                                    Validate.checkLevels(commandContext.getSource().getServer());
                                    for (ServerPlayer player : commandContext.getSource().getServer().getPlayerList().getPlayers()) {
                                        Network.sendProduce(commandContext.getSource().getLevel(), player);
                                    }
                                    commandContext.getSource().sendSuccess(() -> Component.translatable("commands.compost.config.reset"), true);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("produce")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.literal("query")
                                .executes(commandContext -> {
                                    World world = Option.getWorld(commandContext.getSource().getServer().getWorldData().getLevelName());

                                    for (Produce produce : world.produce) {
                                        int index = world.getProduce().indexOf(produce) + 1;
                                        String percentage = new DecimalFormat("0.###############").format(BigDecimal.valueOf(produce.getChance()).multiply(BigDecimal.valueOf(100)));
                                        String count = produce.getMin() != produce.getMax() ? produce.getMin() + "-" + produce.getMax() : String.valueOf(produce.getMax());
                                        Component displayName = Parse.createItemStack(produce).getDisplayName();

                                        commandContext.getSource().sendSuccess(() -> Component.translatable("commands.compost.produce.query", index, percentage, count, displayName), false);
                                    }
                                    return 1;
                                })
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("item", ItemArgument.item(context))
                                        .then(Commands.argument("chance", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                .then(Commands.argument("min", IntegerArgumentType.integer(0, 64))
                                                        .then(Commands.argument("max", IntegerArgumentType.integer(1, 64))
                                                                .executes(commandContext -> {
                                                                    ItemInput itemInput = ItemArgument.getItem(commandContext, "item");
                                                                    ItemStack itemStack = itemInput.createItemStack(1, false);

                                                                    String item = itemInput.serialize(commandContext.getSource().getLevel().registryAccess());
                                                                    double chance = Math.max(0.0D, Math.min(DoubleArgumentType.getDouble(commandContext, "chance"), 1.0D));
                                                                    int min = Math.min(Math.min(IntegerArgumentType.getInteger(commandContext, "min"), itemStack.getMaxStackSize()), IntegerArgumentType.getInteger(commandContext, "max"));
                                                                    int max = Math.max(Math.min(IntegerArgumentType.getInteger(commandContext, "max"), itemStack.getMaxStackSize()), IntegerArgumentType.getInteger(commandContext, "min"));

                                                                    String name = Option.getWorld(commandContext.getSource().getServer().getWorldData().getLevelName()).getName();
                                                                    Produce produce = new Produce(item, chance, min, max);

                                                                    String percentage = new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100)));
                                                                    String count = min != max ? min + "-" + max : String.valueOf(max);
                                                                    Component displayName = itemStack.getDisplayName();

                                                                    Option.addProduce(name, produce);
                                                                    for (ServerPlayer player : commandContext.getSource().getServer().getPlayerList().getPlayers()) {
                                                                        Network.sendProduce(commandContext.getSource().getLevel(), player);
                                                                    }
                                                                    commandContext.getSource().sendSuccess(() -> Component.translatable("commands.compost.produce.add", percentage, count, displayName), true);
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("produce", ProduceArgument.produce())
                                        .executes(commandContext -> {
                                            String name = Option.getWorld(commandContext.getSource().getServer().getWorldData().getLevelName()).getName();
                                            Produce produce = ProduceArgument.getProduce(commandContext, "produce");

                                            Component displayName = Parse.createItemStack(produce).getDisplayName();

                                            Option.removeProduce(name, produce);
                                            for (ServerPlayer player : commandContext.getSource().getServer().getPlayerList().getPlayers()) {
                                                Network.sendProduce(commandContext.getSource().getLevel(), player);
                                            }
                                            commandContext.getSource().sendSuccess(() -> Component.translatable("commands.compost.produce.remove", displayName), true);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }

}