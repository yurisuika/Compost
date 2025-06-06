package dev.yurisuika.compost.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.yurisuika.compost.util.Network;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LoadedWorldArgument implements ArgumentType<String> {

    public LoadedWorldArgument() {}

    public static LoadedWorldArgument loadedWorld() {
        return new LoadedWorldArgument();
    }

    public static String getLoadedWorld(CommandContext<CommandSourceStack> context, String id) {
        return context.getArgument(id, String.class);
    }

    @Override
    public String parse(StringReader reader) {
        final String world = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        return world;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(new HashSet<>() {{ add(Network.getLevelName()); }}, suggestionsBuilder);
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("\"New World\"", "world", "404");
    }

}