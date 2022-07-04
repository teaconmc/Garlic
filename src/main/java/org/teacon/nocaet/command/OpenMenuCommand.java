package org.teacon.nocaet.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class OpenMenuCommand {

    public static void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.then(
            Commands.literal("open")
                .then(Commands.argument("target", EntityArgument.player())
                    .requires(GarlicCommands.hasPermission(GarlicCommands.OPEN_OTHER))
                    .executes(context -> open(context, EntityArgument.getPlayer(context, "target"))))
                .requires(GarlicCommands.hasPermission(GarlicCommands.OPEN_SELF))
                .executes(context -> open(context, context.getSource().getPlayerOrException()))
        );
    }

    private static int open(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        // todo
        return 1;
    }
}
