package org.teacon.nocaet.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super("nocaetreload", "nocaet.command.reload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            ((GarlicBungee) ProxyServer.getInstance().getPluginManager().getPlugin("noCaeT")).loadData();
            sender.sendMessage(TextComponent.fromLegacyText("Reloaded"));
        } catch (IOException e) {
            sender.sendMessage(TextComponent.fromLegacyText(e.toString()));
            e.printStackTrace();
        }
    }
}
