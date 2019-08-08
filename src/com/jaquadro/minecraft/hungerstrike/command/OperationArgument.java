package com.jaquadro.minecraft.hungerstrike.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.EnumUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OperationArgument implements ArgumentType<OperationArgument.CommandOp>
{
    private static final Collection<String> EXAMPLES = Arrays.asList("add", "remove", "mode");
    private static final Collection<String> OPS = Arrays.asList("list", "add", "remove", "mode", "setmode");

    public static final DynamicCommandExceptionType opException = new DynamicCommandExceptionType((arg) -> {
        return new TextComponentTranslation("hungerstrike.argument.operation.invalid", new Object[]{ arg });
    });

    private OperationArgument() { }

    public static OperationArgument operation() {
        return new OperationArgument();
    }

    public static CommandOp getOperation(CommandContext<CommandSource> context, String arg) {
        return context.getArgument(arg, CommandOp.class);
    }

    @Override
    public CommandOp parse(StringReader reader) throws CommandSyntaxException {
        String arg = reader.readUnquotedString();
        if (EnumUtils.isValidEnum(CommandOp.class, arg)) {
            return CommandOp.valueOf(arg);
        } else {
            throw opException.create(arg);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(CommandOp.getValidValues(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public enum CommandOp {
        LIST("list"),
        ADD("add"),
        REMOVE("remove"),
        MODE("mode"),
        SETMODE("setmode");

        private String op;

        private CommandOp(String op) {
            this.op = op;
        }

        public static Collection<String> getValidValues() {
            List<String> names = Lists.newArrayList();
            for (CommandOp op : CommandOp.values()) {
                names.add(op.op);
            }
            return names;
        }
    }
}
