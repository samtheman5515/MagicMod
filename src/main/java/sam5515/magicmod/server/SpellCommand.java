package sam5515.magicmod.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import sam5515.magicmod.common.api.MagicUser;
import sam5515.magicmod.common.api.spell.Spell;
import sam5515.magicmod.common.lib.MagicUserManager;
import sam5515.magicmod.common.network.MMNetwork;
import sam5515.magicmod.common.network.UpdateEquippedSpellsMessage;
import sam5515.magicmod.common.network.UpdateKnownSpellsMessage;
import sam5515.magicmod.common.registry.SpellRegistry;

public class SpellCommand {
    public static int learn(CommandSourceStack source, Spell spell, int level) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        MagicUser magicUser = MagicUserManager.getUser(player);
        magicUser.learnSpell(spell, level - 1);
        if (magicUser.hasBeenModified()){
            source.sendSuccess(() -> Component.literal("You have learned " + SpellRegistry.getRegistry().getKey(spell) + " " + level), true);
            MMNetwork.sendToClient(player, new UpdateKnownSpellsMessage(magicUser));
        } else {
            source.sendFailure(Component.literal("Could not learn spell"));
        }
        return 1;
    }
    public static int equip(CommandSourceStack source, Spell spell, int level, int slot) throws CommandSyntaxException{
        ServerPlayer player = source.getPlayerOrException();
        MagicUser magicUser = MagicUserManager.getUser(player);
        magicUser.equipSpell(slot, spell, level);
        if(magicUser.hasBeenModified()){
            source.sendSuccess(() -> Component.literal("Successfully equipped spell"), true);
            MMNetwork.sendToClient(player, new UpdateEquippedSpellsMessage(magicUser.allEquippedSpells(), true));
        } else {
            source.sendFailure(Component.literal("Could not equip spell"));
        }
        return 1;
    }
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        var builder = Commands.literal("spell")
                .then(Commands.literal("learn")
                        .then(Commands.argument("spell", SpellArgumentType.spell())
                                .executes(context -> learn(context.getSource(), SpellArgumentType.getSpell(context, "spell"), 1))
                                .then(Commands.argument("level", IntegerArgumentType.integer(1, 127))
                                        .executes(context -> learn(context.getSource(), SpellArgumentType.getSpell(context, "spell"), IntegerArgumentType.getInteger(context, "level")))
                                )
                        )
                ).then(Commands.literal("equip")
                        .then(Commands.argument("spell", SpellArgumentType.spell())
                                .then(Commands.argument("level", IntegerArgumentType.integer(1, 127))
                                        .then(Commands.argument("slot", IntegerArgumentType.integer(0, MagicUser.EQUIP_SLOTS))
                                                .executes(context -> equip(context.getSource(),
                                                        SpellArgumentType.getSpell(context, "spell"),
                                                        IntegerArgumentType.getInteger(context, "level"),
                                                        IntegerArgumentType.getInteger(context, "slot")
                                                ))
                                        )
                                )
                        )
                );
        dispatcher.register(builder);
    }
}
