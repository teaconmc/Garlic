package org.teacon.nocaet.bungee.network.packet;

import io.netty.buffer.ByteBuf;
import org.teacon.nocaet.bungee.network.Packet;

import java.util.List;
import java.util.UUID;

public record SetFlames(UUID uuid, List<String> flames) implements Packet {

    public SetFlames(ByteBuf buf) {
        this(null, null);
        throw new RuntimeException();
    }

    @Override
    public void write(ByteBuf buf) {
        Packet.writeUUID(uuid, buf);
        Packet.writeStringArray(flames, buf);
    }
}
