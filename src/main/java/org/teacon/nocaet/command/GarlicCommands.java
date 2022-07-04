package org.teacon.nocaet.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import org.teacon.nocaet.GarlicMod;

import java.util.function.Predicate;

public class GarlicCommands {

    public static final PermissionNode<Boolean> OPEN_SELF = bool("open.self", 0);
    public static final PermissionNode<Boolean> OPEN_OTHER = bool("open.other", 2);
    public static final PermissionNode<Boolean> SEND_PROGRESS = bool("progress", 2);

    private static PermissionNode<Boolean> bool(String name, int level) {
        return new PermissionNode<>(GarlicMod.MODID, name, PermissionTypes.BOOLEAN,
            (player, playerUUID, context) -> player != null && player.hasPermissions(level));
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(GarlicCommands::register);
        MinecraftForge.EVENT_BUS.addListener(GarlicCommands::registerPermission);
    }

    private static void register(RegisterCommandsEvent event) {
        var builder = Commands.literal("nocaet");
        OpenMenuCommand.register(builder);
        SetProgressCommand.register(builder);
        event.getDispatcher().register(builder);
    }

    private static void registerPermission(PermissionGatherEvent.Nodes event) {
        event.addNodes(OPEN_SELF, OPEN_OTHER, SEND_PROGRESS);
    }

    static Predicate<CommandSourceStack> hasPermission(PermissionNode<Boolean> node) {
        return stack -> {
            if (stack.getEntity() instanceof ServerPlayer player) {
                return PermissionAPI.getPermission(player, node);
            } else {
                return true;
            }
        };
    }
}
