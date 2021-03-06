package org.teacon.nocaet.bungee.network.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import org.teacon.nocaet.bungee.ServerGroup;
import org.teacon.nocaet.bungee.network.Packet;
import org.teacon.nocaet.bungee.network.Registry;

public record SyncProgress(float progress) implements Packet {

    public SyncProgress(ByteBuf buf) {
        this(buf.readFloat());
    }

    @Override
    public void handle(Server sender) {
        for (ServerInfo info : ServerGroup.instance().getOtherServers(sender.getInfo())) {
            Registry.instance().send(this, info);
        }
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeFloat(progress);
    }
}
