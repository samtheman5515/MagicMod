package sam5515.magicmod.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import sam5515.magicmod.common.api.MagicUser;
import sam5515.magicmod.common.lib.MagicUserManager;
import sam5515.magicmod.common.network.MMNetwork;
import sam5515.magicmod.common.network.UpdateManaMessage;

public class ManaCommand {
    private static int setMana(CommandSourceStack source, double mana) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        MagicUser magicUser = MagicUserManager.getUser(player);
        magicUser.setMana(mana);
        if (magicUser.hasBeenModified()){
            MMNetwork.sendToClient(player, new UpdateManaMessage(magicUser));
        }
        source.sendSuccess(() -> Component.literal("Set mana to " + magicUser.getMana()), true);
        return 1;
    }
    private static int setMaxMana(CommandSourceStack source, double maxMana) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        MagicUser magicUser = MagicUserManager.getUser(player);
        magicUser.setMaxMana(maxMana);
        if (magicUser.hasBeenModified()){
            MMNetwork.sendToClient(player, new UpdateManaMessage(magicUser));
        }
        source.sendSuccess(() -> Component.literal("Set max mana to " + magicUser.getMaxMana()), true);
        return 1;
    }
    private static int setRechargeRate(CommandSourceStack source, double rechargeRate) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        MagicUser magicUser = MagicUserManager.getUser(player);
        magicUser.setRechargeRate(rechargeRate);
        if (magicUser.hasBeenModified()){
            MMNetwork.sendToClient(player, new UpdateManaMessage(magicUser));
        }
        source.sendSuccess(() -> Component.literal("Set recharge rate to " + magicUser.getRechargeRate()), true);
        return 1;
    }
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        var builder = Commands.literal("mana")
                .then(Commands.literal("set")
                        .then(Commands.argument("mana", DoubleArgumentType.doubleArg(0, Double.MAX_VALUE))
                                .executes(context -> setMana(context.getSource(), DoubleArgumentType.getDouble(context, "mana")))
                        )
                )
                .then(Commands.literal("setMax")
                        .then(Commands.argument("maxMana", DoubleArgumentType.doubleArg(0, Double.MAX_VALUE))
                                .executes(context -> setMaxMana(context.getSource(), DoubleArgumentType.getDouble(context, "maxMana")))
                        )
                )
                .then(Commands.literal("setRate")
                        .then(Commands.argument("rate", DoubleArgumentType.doubleArg(0, Double.MAX_VALUE))
                                .executes(context -> setRechargeRate(context.getSource(), DoubleArgumentType.getDouble(context, "rate")))
                        )
                );
        dispatcher.register(builder);
    }
}
