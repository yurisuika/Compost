package dev.yurisuika.compost.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.yurisuika.compost.util.Parse;
import dev.yurisuika.compost.util.Validate;
import dev.yurisuika.compost.util.config.Option;
import dev.yurisuika.compost.util.config.options.Produce;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParserUtils;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProduceArgument implements ArgumentType<Produce> {

    public static ProduceArgument produce() {
        return new ProduceArgument();
    }

    public static Produce getProduce(CommandContext<CommandSourceStack> context, String id) {
        return context.getArgument(id, Produce.class);
    }

    public Produce parse(StringReader reader) throws CommandSyntaxException {
        String string = ParserUtils.readWhile(reader, c -> c != ' ');
        try {
            return Option.getProduce(Validate.getLevelName()).get(Parse.listItems().indexOf(string));
        } catch (Exception e) {
            throw new DynamicCommandExceptionType(object -> Component.translatableEscape("commands.compost.produce.unknown", object)).createWithContext(reader, string);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(Parse.listItems(), suggestionsBuilder);
    }

    public Collection<String> getExamples() {
        return List.of("minecraft:apple", "minecraft:enchanted_golden_apple", "minecraft:bread[minecraft:enchantments={levels:{\"minecraft:knockback\":2}}]");
    }

}