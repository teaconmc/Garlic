package org.teacon.nocaet.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.Objects;

public final class GarlicBungee extends Plugin implements Listener {

    @Override
    public void onEnable() {
        this.getProxy().registerChannel("nocaet:proxy");
    }

    @EventHandler
    public void onSync(PluginMessageEvent event) {
        var sender = event.getSender();
        if ("nocaet:proxy".equals(event.getTag())) {
            // drop message from client
            event.setCancelled(true);
            if (sender instanceof Server server) {
                for (ServerInfo info : this.getProxy().getServers().values()) {
                    if (!Objects.equals(info, server.getInfo())) {
                        info.sendData(event.getTag(), event.getData());
                    }
                }
            }
        }
    }
}
