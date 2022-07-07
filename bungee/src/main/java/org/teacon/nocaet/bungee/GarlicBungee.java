package org.teacon.nocaet.bungee;

import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.teacon.nocaet.bungee.network.Registry;
import org.teacon.nocaet.bungee.network.packet.SetFlames;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public final class GarlicBungee extends Plugin implements Listener {

    private Path data;
    private final PlayerData playerData = new PlayerData();

    @Override
    public void onEnable() {
        this.getProxy().registerChannel("nocaet:proxy");
        this.getProxy().getScheduler().schedule(this, this::save, 1, 1, TimeUnit.HOURS);
        this.getProxy().getPluginManager().registerCommand(this, new ReloadCommand());
        try {
            this.loadData();
        } catch (IOException e) {
            getLogger().severe(e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        this.save();
    }

    synchronized void loadData() throws IOException {
        Files.createDirectories(this.getDataFolder().toPath());
        ServerGroup.instance().reload();
        this.data = new File(this.getDataFolder(), "data.yml").toPath();
        if (Files.exists(this.data)) {
            this.playerData.load(this.data);
        }
    }

    private synchronized void save() {
        try {
            this.playerData.save(this.data);
        } catch (IOException e) {
            this.getLogger().severe("Error saving data");
            e.printStackTrace();
        }
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    @EventHandler
    public void onSync(PluginMessageEvent event) {
        var sender = event.getSender();
        if ("nocaet:proxy".equals(event.getTag())) {
            // drop message from client
            event.setCancelled(true);
            if (sender instanceof Server server) {
                var packet = Registry.instance().decode(event.getData());
                packet.handle(server);
            }
        }
    }

    @EventHandler
    public void onJoin(ServerSwitchEvent event) {
        var server = event.getPlayer().getServer();
        var uuid = event.getPlayer().getUniqueId();
        var list = getPlayerData().getAll(server.getInfo(), uuid);
        if (list != null) {
            Registry.instance().send(new SetFlames(uuid, list), server.getInfo());
        }
    }
}
