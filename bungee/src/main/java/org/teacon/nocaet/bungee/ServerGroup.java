package org.teacon.nocaet.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ServerGroup {

    private static final ServerGroup INSTANCE = new ServerGroup();

    public static ServerGroup instance() {
        return INSTANCE;
    }

    private final Map<String, Set<String>> groupToServer = new HashMap<>();
    private final Map<String, String> serverToGroup = new HashMap<>();
    private String defaultGroup;

    public void reload() throws IOException {
        var provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
        var plugin = (GarlicBungee) ProxyServer.getInstance().getPluginManager().getPlugin("noCaeT");
        var config = plugin.getDataFolder().toPath().resolve("config.yml");
        if (!Files.exists(config)) {
            Files.write(config, plugin.getResourceAsStream("config.yml").readAllBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        }
        var configuration = provider.load(config.toFile());
        var groups = configuration.getList("groups", Collections.emptyList());
        for (var sec : groups) {
            if (sec instanceof Map map) {
                var pattern = map.get("pattern").toString();
                var group = map.get("group").toString();
                var servers = ProxyServer.getInstance().getServers().values().stream().map(ServerInfo::getName)
                    .filter(it -> it.matches(pattern)).collect(Collectors.toSet());
                groupToServer.put(group, servers);
                for (var server : servers) {
                    serverToGroup.putIfAbsent(server, group);
                }
            }
        }
        this.defaultGroup = configuration.getString("default");
    }

    public Collection<String> getGroups() {
        return groupToServer.keySet();
    }

    public String getGroup(ServerInfo info) {
        return serverToGroup.getOrDefault(info.getName(), this.defaultGroup);
    }

    public Collection<ServerInfo> getOtherServers(ServerInfo server) {
        var s = getGroup(server);
        if (s != null) {
            return groupToServer.get(s).stream().map(ProxyServer.getInstance()::getServerInfo).filter(it -> it != server).toList();
        } else {
            return Collections.emptyList();
        }
    }

    public Collection<ServerInfo> getServers(ServerInfo server) {
        var s = getGroup(server);
        if (s != null) {
            return groupToServer.get(s).stream().map(ProxyServer.getInstance()::getServerInfo).toList();
        } else {
            return Collections.emptyList();
        }
    }
}
