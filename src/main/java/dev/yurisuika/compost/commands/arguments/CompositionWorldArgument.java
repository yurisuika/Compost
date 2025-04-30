package dev.yurisuika.compost.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.yurisuika.compost.util.Network;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CompositionWorldArgument implements ArgumentType<String> {

    public CompositionWorldArgument() {}

    public static CompositionWorldArgument compositionWorld() {
        return new CompositionWorldArgument();
    }

    public static String getCompositionWorld(CommandContext<CommandSourceStack> context, String id) {
        return context.getArgument(id, String.class);
    }

    public String parse(StringReader reader) throws CommandSyntaxException {
        final String world = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        if (Network.getNetworkCompositions().get(CompositionArgument.getName()).getWorlds().contains(world)) {
            return world;
        } else {
            throw new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.compost.composition.world.unknown", object)).createWithContext(reader, world);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(Network.getNetworkCompositions().get(CompositionArgument.getName()).getWorlds(), suggestionsBuilder);
    }

    public Collection<String> getExamples() {
        return List.of("\"New World\"", "world", "404");
    }

}