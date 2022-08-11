package org.teacon.nocaet.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.teacon.nocaet.bungee.network.Registry;
import org.teacon.nocaet.bungee.network.packet.SetFlames;
import org.teacon.nocaet.bungee.network.packet.SyncProgress;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public final class GarlicBungee extends Plugin implements Listener {

    private final PlayerData playerData = new PlayerData();
    private String footer;

    @Override
    public void onEnable() {
        this.getProxy().registerChannel("nocaet:proxy");
        this.getProxy().getPluginManager().registerListener(this, this);
        this.getProxy().getScheduler().schedule(this, this::save, 1, 1, TimeUnit.HOURS);
        this.getProxy().getPluginManager().registerCommand(this, new GarlicCommand());
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
        var configuration = ServerGroup.instance().reload(this);
        this.playerData.load(this.getDataFolder().toPath());
        this.footer = configuration.getString("tab_footer");
    }

    private synchronized void save() {
        try {
            this.playerData.save(this.getDataFolder().toPath());
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
        event.getPlayer().setTabHeader(null, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', this.footer.formatted(list == null ? 0 : list.size()))));
        var optional = getPlayerData().getProgress(server.getInfo());
        if (optional.isPresent()) {
            var progress = optional.get();
            Registry.instance().sendToClient(new SyncProgress(progress), event.getPlayer());
        }
    }

    public void updateFooter(ProxiedPlayer player) {
        var list = getPlayerData().getAll(player.getServer().getInfo(), player.getUniqueId());
        player.setTabHeader(null, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', this.footer.formatted(list == null ? 0 : list.size()))));
    }
}
