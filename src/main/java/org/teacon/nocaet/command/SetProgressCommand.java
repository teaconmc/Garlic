package org.teacon.nocaet.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.teacon.nocaet.network.GarlicChannel;
import org.teacon.nocaet.network.play.SetProgressPacket;
import org.teacon.nocaet.network.proxy.SyncProgressPacket;

public class SetProgressCommand {

    public static void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.then(
            Commands.literal("progress")
                .then(Commands.argument("value", FloatArgumentType.floatArg(0, 1))
                    .requires(GarlicCommands.hasPermission(GarlicCommands.SEND_PROGRESS))
                    .executes(SetProgressCommand::sendProgress))
        );
    }

    private static int sendProgress(CommandContext<CommandSourceStack> context) {
        var progress = FloatArgumentType.getFloat(context, "value");
        GarlicChannel.getChannel().send(PacketDistributor.ALL.noArg(), new SetProgressPacket(progress));
        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().stream().findAny().ifPresent(it ->
            GarlicChannel.sendProxy(PacketDistributor.PLAYER.with(() -> it), new SyncProgressPacket(progress)));
        context.getSource().sendSuccess(new TranslatableComponent("nocaet.command.progress", String.format("%.3f", progress)), false);
        return 1;
    }
}
