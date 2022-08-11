package org.teacon.nocaet.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GarlicCommand extends Command {

    public GarlicCommand() {
        super("nocaetbc", "nocaet.command.reload", "nbc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            if (args.length >= 1 && "reload".equalsIgnoreCase(args[0])) {
                ((GarlicBungee) ProxyServer.getInstance().getPluginManager().getPlugin("noCaeT")).loadData();
                sender.sendMessage(TextComponent.fromLegacyText("Reloaded"));
            }
            if (args.length >= 1 && "stat".equalsIgnoreCase(args[0])) {
                var group = args.length > 1 ? args[1] : "prod";
                var data = ((GarlicBungee) ProxyServer.getInstance().getPluginManager().getPlugin("noCaeT")).getPlayerData();
                var map = data.getGroup(group);
                var stat = map.values().stream().flatMap(Collection::stream)
                    .collect(Collectors.groupingBy(PlayerData.Claim::name, Collectors.counting()))
                    .entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).toList();
                sender.sendMessage(TextComponent.fromLegacyText("There are " + map.size() + " players and " + map.values().stream().mapToInt(Set::size).sum() + " entries in " + group));
                var size = (float) map.size();
                for (var entry : stat) {
                    sender.sendMessage(TextComponent.fromLegacyText("%6.2f%% %6d %s".formatted(entry.getValue() / size * 100F, entry.getValue(), entry.getKey())));
                }
            }
            if (args.length >= 1 && "top".equalsIgnoreCase(args[0])) {
                var group = args.length > 1 ? args[1] : "prod";
                var data = ((GarlicBungee) ProxyServer.getInstance().getPluginManager().getPlugin("noCaeT")).getPlayerData();
                var map = data.getGroup(group);
                var stat = map.entrySet().stream()
                    .filter(it -> !it.getValue().isEmpty())
                    .sorted(Map.Entry.comparingByValue(Comparator
                        .<Set<PlayerData.Claim>, Integer>comparing(Set::size).reversed() // count desc
                        .thenComparing(it -> it.stream().max(Comparator.comparing(PlayerData.Claim::instant)).get().instant()))) // last time asc
                    .map(it -> new AbstractMap.SimpleImmutableEntry<>(it.getKey(), it.getValue().size())).limit(16).toList();
                for (var entry : stat) {
                    var player = ProxyServer.getInstance().getPlayer(entry.getKey());
                    sender.sendMessage(TextComponent.fromLegacyText("%s %s".formatted(player == null ? entry.getKey() : player.getName(), entry.getValue())));
                }
            }
        } catch (IOException e) {
            sender.sendMessage(TextComponent.fromLegacyText(e.toString()));
            e.printStackTrace();
        }
    }
}
