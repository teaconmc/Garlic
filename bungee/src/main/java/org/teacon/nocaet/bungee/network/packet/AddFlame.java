package org.teacon.nocaet.bungee.network.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Server;
import org.teacon.nocaet.bungee.GarlicBungee;
import org.teacon.nocaet.bungee.network.Packet;

import java.util.UUID;

public record AddFlame(UUID uuid, String namespace) implements Packet {

    public AddFlame(ByteBuf buf) {
        this(new UUID(buf.readLong(), buf.readLong()), Packet.readString(buf));
    }

    @Override
    public void handle(Server sender) {
        var plugin = (GarlicBungee) ProxyServer.getInstance().getPluginManager().getPlugin("noCaeT");
        var playerData = plugin.getPlayerData();
        playerData.add(sender.getInfo(), uuid, namespace);
        plugin.getLogger().info("" + uuid + " claimed " + namespace);
        // todo calculate progress and broadcast
    }

    @Override
    public void write(ByteBuf buf) {
        Packet.writeUUID(uuid, buf);
        Packet.writeString(namespace, buf);
    }
}
