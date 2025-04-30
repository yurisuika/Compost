package dev.yurisuika.compost.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.yurisuika.compost.util.Network;
import dev.yurisuika.compost.util.Parse;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CompositionArgument implements ArgumentType<String> {

    public static String name;

    public CompositionArgument() {}

    public static CompositionArgument composition() {
        return new CompositionArgument();
    }

    public static String getComposition(CommandContext<CommandSourceStack> context, String id) {
        return context.getArgument(id, String.class);
    }

    public String parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readString();
        if (Network.getNetworkCompositions().containsKey(name)) {
            setName(name);
            return name;
        } else {
            throw new DynamicCommandExceptionType(object -> Component.translatable("commands.compost.composition.unknown", object)).createWithContext(reader, name);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(Parse.listNetworkNames(), suggestionsBuilder);
    }

    public Collection<String> getExamples() {
        return List.of("vanilla", "\"Just Dirt\"", "compost");
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        CompositionArgument.name = name;
    }

}