package org.teacon.nocaet.bungee.network.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.chat.ComponentSerializer;
import org.teacon.nocaet.bungee.GarlicBungee;
import org.teacon.nocaet.bungee.ServerGroup;
import org.teacon.nocaet.bungee.network.Packet;

import java.util.UUID;

public record AddFlame(UUID uuid, String namespace, String item) implements Packet {

    public AddFlame(ByteBuf buf) {
        this(new UUID(buf.readLong(), buf.readLong()), Packet.readString(buf), Packet.readString(buf));
    }

    @Override
    public void handle(Server sender) {
        var plugin = (GarlicBungee) ProxyServer.getInstance().getPluginManager().getPlugin("noCaeT");
        var playerData = plugin.getPlayerData();
        playerData.add(sender.getInfo(), uuid, namespace);
        plugin.getLogger().info("" + uuid + " claimed " + namespace);
        var message = new TranslatableComponent("nocaet.flame.grant",
            ProxyServer.getInstance().getPlayer(uuid).getDisplayName(), getItem());
        for (var server : ServerGroup.instance().getServers(sender.getInfo())) {
            for (var player : server.getPlayers()) {
                player.sendMessage(message);
            }
        }
        // todo calculate progress and broadcast
    }

    @Override
    public void write(ByteBuf buf) {
        Packet.writeUUID(uuid, buf);
        Packet.writeString(namespace, buf);
        Packet.writeString(item, buf);
    }

    public BaseComponent getItem() {
        return ComponentSerializer.parse(item)[0];
    }
}
