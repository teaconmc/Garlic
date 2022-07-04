package org.teacon.nocaet.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.teacon.nocaet.network.GarlicChannel;
import org.teacon.nocaet.network.SetProgressPacket;
import org.teacon.nocaet.network.SyncProgressPacket;

public class SetProgressCommand {

    public static void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.then(
            Commands.literal("progress")
                .then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 1))
                    .requires(GarlicCommands.hasPermission(GarlicCommands.SEND_PROGRESS))
                    .executes(SetProgressCommand::sendProgress))
        );
    }

    private static int sendProgress(CommandContext<CommandSourceStack> context) {
        double progress = DoubleArgumentType.getDouble(context, "value");
        GarlicChannel.getChannel().send(PacketDistributor.ALL.noArg(), new SetProgressPacket(progress));
        // sync only on dedicated servers
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().stream().findAny().ifPresent(it ->
                GarlicChannel.getProxyChannel().send(PacketDistributor.PLAYER.with(() -> it), new SyncProgressPacket(progress)));
        });
        return 1;
    }
}
