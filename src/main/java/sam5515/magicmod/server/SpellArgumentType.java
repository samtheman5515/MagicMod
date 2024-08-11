package sam5515.magicmod.server;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import sam5515.magicmod.common.api.spell.Spell;
import sam5515.magicmod.common.registry.SpellRegistry;

import javax.lang.model.element.Name;
import java.util.concurrent.CompletableFuture;

public class SpellArgumentType implements ArgumentType<Spell> {
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_SPELL = new DynamicCommandExceptionType(name -> {
        return Component.literal("Unknown spell: " + name);
    });
    @Override
    public Spell parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation name = ResourceLocation.read(reader);
        Spell spell = SpellRegistry.getRegistry().getValue(name);
        if(spell == null){
            throw ERROR_UNKNOWN_SPELL.create(name);
        }
        return spell;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(SpellRegistry.getRegistry().getKeys(), builder);
    }
    public static SpellArgumentType spell(){
        return new SpellArgumentType();
    }
    public static Spell getSpell(CommandContext<CommandSourceStack> context, String argumentName){
        return context.getArgument(argumentName, Spell.class);
    }
}
