package org.teacon.nocaet.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.ForgeRegistries;
import org.teacon.nocaet.GarlicRegistry;
import org.teacon.nocaet.network.capability.GarlicCapability;

import java.util.function.Function;
import java.util.stream.Collectors;

public class LookupCommand {

    public static void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.then(
            Commands.literal("lookup")
                .then(Commands.argument("target", EntityArgument.player())
                    .requires(GarlicCommands.hasPermission(GarlicCommands.OPEN_OTHER))
                    .executes(context -> open(context, EntityArgument.getPlayer(context, "target"))))
                .requires(GarlicCommands.hasPermission(GarlicCommands.OPEN_SELF))
                .executes(context -> open(context, context.getSource().getPlayerOrException()))
        );
    }

    private static int open(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        target.getCapability(GarlicCapability.flames()).ifPresent(it -> {
            var flames = ForgeRegistries.ITEMS.tags().getTag(GarlicRegistry.FLAME_TAG).stream()
                .collect(Collectors.toMap(Function.identity(), item -> it.contains(item.getRegistryName())));
            context.getSource().sendSuccess(new TranslatableComponent("nocaet.command.lookup", target.getDisplayName(), it.getGranted().size(), flames.size()), false);
            for (var entry : flames.entrySet()) {
                var status = new TranslatableComponent("nocaet.command.lookup." + (entry.getValue() ? "granted" : "not_granted"))
                    .withStyle(entry.getValue() ? ChatFormatting.GREEN : ChatFormatting.RED);
                context.getSource().sendSuccess(status.append(entry.getKey().getDefaultInstance().getDisplayName()), false);
            }
        });
        return 1;
    }
}
