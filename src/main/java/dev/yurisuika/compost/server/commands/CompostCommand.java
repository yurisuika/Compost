package dev.yurisuika.compost.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.yurisuika.compost.commands.arguments.CompositionArgument;
import dev.yurisuika.compost.commands.arguments.CompositionWorldArgument;
import dev.yurisuika.compost.commands.arguments.LoadedWorldArgument;
import dev.yurisuika.compost.config.Config;
import dev.yurisuika.compost.config.Options;
import dev.yurisuika.compost.util.Configure;
import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import dev.yurisuika.compost.util.Validate;
import dev.yurisuika.compost.world.Composition;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashSet;

public class CompostCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, CommandSelection selection) {
        dispatcher.register(Commands.literal("compost")
                .requires(source -> source.hasPermission(4))
                .then(Commands.literal("config")
                        .then(Commands.literal("reload")
                                .executes(commandContext -> reloadConfig(
                                        commandContext.getSource()
                                ))
                        )
                        .then(Commands.literal("reset")
                                .executes(commandContext -> resetConfig(
                                        commandContext.getSource()
                                ))
                        )
                )
                .then(Commands.literal("composition")
                        .then(Commands.literal("query")
                                .executes(commandContext -> queryCompositions(
                                                commandContext.getSource()
                                        )
                                )
                                .then(Commands.argument("name", CompositionArgument.composition())
                                        .executes(commandContext -> queryComposition(
                                                        commandContext.getSource(),
                                                        CompositionArgument.getComposition(commandContext, "name")
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .then(Commands.argument("item", ItemArgument.item(context))
                                                .executes(commandContext -> addComposition(
                                                                commandContext.getSource(),
                                                                StringArgumentType.getString(commandContext, "name"),
                                                                ItemArgument.getItem(commandContext, "item"),
                                                                1.0D,
                                                                1,
                                                                1,
                                                                null
                                                        )
                                                )
                                                .then(Commands.argument("chance", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                        .then(Commands.argument("min", IntegerArgumentType.integer(1, 64))
                                                                .then(Commands.argument("max", IntegerArgumentType.integer(1, 64))
                                                                        .executes(commandContext -> addComposition(
                                                                                        commandContext.getSource(),
                                                                                        StringArgumentType.getString(commandContext, "name"),
                                                                                        ItemArgument.getItem(commandContext, "item"),
                                                                                        DoubleArgumentType.getDouble(commandContext, "chance"),
                                                                                        IntegerArgumentType.getInteger(commandContext, "min"),
                                                                                        IntegerArgumentType.getInteger(commandContext, "max"),
                                                                                        null
                                                                                )
                                                                        )
                                                                        .then(Commands.argument("world", LoadedWorldArgument.loadedWorld())
                                                                                .executes(commandContext -> addComposition(
                                                                                                commandContext.getSource(),
                                                                                                StringArgumentType.getString(commandContext, "name"),
                                                                                                ItemArgument.getItem(commandContext, "item"),
                                                                                                DoubleArgumentType.getDouble(commandContext, "chance"),
                                                                                                IntegerArgumentType.getInteger(commandContext, "min"),
                                                                                                IntegerArgumentType.getInteger(commandContext, "max"),
                                                                                                LoadedWorldArgument.getLoadedWorld(commandContext, "world")
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("name", CompositionArgument.composition())
                                        .executes(commandContext -> removeComposition(
                                                        commandContext.getSource(),
                                                        CompositionArgument.getComposition(commandContext, "name")
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("modify")
                                .then(Commands.argument("name", CompositionArgument.composition())
                                        .then(Commands.literal("compost")
                                                .then(Commands.literal("item")
                                                        .then(Commands.argument("item", ItemArgument.item(context))
                                                                .executes(commandContext -> modifyCompositionCompostItem(
                                                                                commandContext.getSource(),
                                                                                CompositionArgument.getComposition(commandContext, "name"),
                                                                                ItemArgument.getItem(commandContext, "item")
                                                                        )
                                                                )
                                                        )
                                                )
                                                .then(Commands.literal("chance")
                                                        .then(Commands.argument("chance", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                                .executes(commandContext -> modifyCompositionCompostChance(
                                                                                commandContext.getSource(),
                                                                                CompositionArgument.getComposition(commandContext, "name"),
                                                                                DoubleArgumentType.getDouble(commandContext, "chance")
                                                                        )
                                                                )
                                                        )
                                                )
                                                .then(Commands.literal("count")
                                                        .then(Commands.literal("min")
                                                                .then(Commands.argument("min", IntegerArgumentType.integer(0, 64))
                                                                        .executes(commandContext -> modifyCompositionCompostCountMin(
                                                                                        commandContext.getSource(),
                                                                                        CompositionArgument.getComposition(commandContext, "name"),
                                                                                        IntegerArgumentType.getInteger(commandContext, "min")
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                        .then(Commands.literal("max")
                                                                .then(Commands.argument("max", IntegerArgumentType.integer(0, 64))
                                                                        .executes(commandContext -> modifyCompositionCompostCountMax(
                                                                                        commandContext.getSource(),
                                                                                        CompositionArgument.getComposition(commandContext, "name"),
                                                                                        IntegerArgumentType.getInteger(commandContext, "max")
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                        .then(Commands.literal("worlds")
                                                .then(Commands.literal("add")
                                                        .executes(commandContext -> modifyCompositionWorldAdd(
                                                                        commandContext.getSource(),
                                                                        CompositionArgument.getComposition(commandContext, "name"),
                                                                        commandContext.getSource().getServer().getWorldData().getLevelName()
                                                                )
                                                        )
                                                        .then(Commands.argument("world", LoadedWorldArgument.loadedWorld())
                                                                .executes(commandContext -> modifyCompositionWorldAdd(
                                                                                commandContext.getSource(),
                                                                                CompositionArgument.getComposition(commandContext, "name"),
                                                                                LoadedWorldArgument.getLoadedWorld(commandContext, "world")
                                                                        )
                                                                )
                                                        )
                                                )
                                                .then(Commands.literal("remove")
                                                        .executes(commandContext -> modifyCompositionWorldRemove(
                                                                        commandContext.getSource(),
                                                                        CompositionArgument.getComposition(commandContext, "name"),
                                                                        commandContext.getSource().getServer().getWorldData().getLevelName()
                                                                )
                                                        )
                                                        .then(Commands.argument("world", CompositionWorldArgument.compositionWorld())
                                                                .executes(commandContext -> modifyCompositionWorldRemove(
                                                                                commandContext.getSource(),
                                                                                CompositionArgument.getComposition(commandContext, "name"),
                                                                                CompositionWorldArgument.getCompositionWorld(commandContext, "world")
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    public static int reloadConfig(CommandSourceStack source) {
        Config.loadConfig();
        Validate.validateCompositions();

        sendCompositions(source);
        source.sendSuccess(() -> Component.translatable("commands.compost.config.reload"), true);
        return 1;
    }

    public static int resetConfig(CommandSourceStack source) {
        Configure.setCompositions(new Options().getCompositions());

        sendCompositions(source);
        source.sendSuccess(() -> Component.translatable("commands.compost.config.reset"), true);
        return 1;
    }

    public static int queryComposition(CommandSourceStack source, String name) {
        Composition composition = Configure.getComposition(name);

        createCompositionMessage(source, name, composition, "commands.compost.composition.query");
        return 1;
    }

    public static int queryCompositions(CommandSourceStack source) {
        Configure.getCompositions().forEach((name, composition) -> createCompositionMessage(source, name, composition, "commands.compost.composition.query"));
        return 1;
    }

    public static int addComposition(CommandSourceStack source, String name, ItemInput item, double chance, int min, int max, String world) {
        Composition composition = new Composition(new Composition.Compost(item.serialize(source.getLevel().registryAccess()), chance, new Composition.Compost.Count(min, max)), new HashSet<>() {{ if (world != null) add(world); }});
        Validate.validateComposition(composition);

        Configure.addComposition(name, composition);
        sendCompositions(source);
        createCompositionMessage(source, name, composition, "commands.compost.composition.add");
        return 1;
    }

    public static int removeComposition(CommandSourceStack source, String name) {
        Composition composition = Configure.getComposition(name);

        Configure.removeComposition(name);
        sendCompositions(source);
        createCompositionMessage(source, name, composition, "commands.compost.composition.remove");
        return 1;
    }

    public static int modifyComposition(CommandSourceStack source, String name) {
        Composition composition = Configure.getComposition(name);

        sendCompositions(source);
        createCompositionMessage(source, name, composition, "commands.compost.composition.modify");
        return 1;
    }

    public static int modifyCompositionCompostItem(CommandSourceStack source, String name, ItemInput item) {
        Configure.setItem(name, item.serialize(source.registryAccess()));
        Validate.validateComposition(name);

        return modifyComposition(source, name);
    }

    public static int modifyCompositionCompostChance(CommandSourceStack source, String name, double chance) {
        Configure.setChance(name, chance);
        Validate.validateComposition(name);

        return modifyComposition(source, name);
    }

    public static int modifyCompositionCompostCountMin(CommandSourceStack source, String name, int min) {
        Configure.setMin(name, min);
        Validate.validateComposition(name);

        return modifyComposition(source, name);
    }

    public static int modifyCompositionCompostCountMax(CommandSourceStack source, String name, int max) {
        Configure.setMax(name, max);
        Validate.validateComposition(name);

        return modifyComposition(source, name);
    }

    public static int modifyCompositionWorldAdd(CommandSourceStack source, String name, String world) {
        Configure.addWorld(name, world);
        Validate.validateComposition(name);

        return modifyComposition(source, name);
    }

    public static int modifyCompositionWorldRemove(CommandSourceStack source, String name, String world) {
        Configure.removeWorld(name, world);
        Validate.validateComposition(name);

        return modifyComposition(source, name);
    }

    public static void sendCompositions(CommandSourceStack source) {
        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Network.sendCompositions(source.getLevel(), player);
        }
    }

    public static void createCompositionMessage(CommandSourceStack source, String name, Composition composition, String translation) {
        MutableComponent compositionName = ComponentUtils.wrapInSquareBrackets(Component.literal(name)).withStyle(style -> style.withColor(composition.getWorlds().isEmpty() || composition.getWorlds().contains(source.getServer().getWorldData().getLevelName()) ? ChatFormatting.GREEN : ChatFormatting.RED).withHoverEvent(new HoverEvent.ShowText(Component.translatable("commands.compost.composition.tooltip", composition.getWorlds().toString()))));
        String percentage = new DecimalFormat("0.###############").format(BigDecimal.valueOf(composition.getCompost().getChance()).multiply(BigDecimal.valueOf(100)));
        String count = composition.getCompost().getCount().getMin() != composition.getCompost().getCount().getMax() ? composition.getCompost().getCount().getMin() + "-" + composition.getCompost().getCount().getMax() : String.valueOf(composition.getCompost().getCount().getMax());
        Component displayName = Parse.createItemStack(source.registryAccess(), composition).getDisplayName();

        source.sendSuccess(() -> Component.translatable(translation, compositionName, percentage, count, displayName), true);
    }

}