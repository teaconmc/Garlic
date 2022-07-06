package org.teacon.nocaet.bungee.network;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.List;
import java.util.UUID;

public interface Packet {

    default void handle(Server sender) {
        throw new RuntimeException(getClass().getSimpleName());
    }

    void write(ByteBuf buf);

    static void writeUUID(UUID uuid, ByteBuf buf) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    static UUID readUUID(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    static void writeString(String s, ByteBuf buf) {
        DefinedPacket.writeString(s, buf);
    }

    static String readString(ByteBuf buf) {
        return readString(buf, Short.MAX_VALUE);
    }

    static String readString(ByteBuf buf, int maxLen) {
        return DefinedPacket.readString(buf, maxLen);
    }

    static void writeStringArray(List<String> s, ByteBuf buf) {
        DefinedPacket.writeStringArray(s, buf);
    }

    static List<String> readStringArray(ByteBuf buf) {
        return DefinedPacket.readStringArray(buf);
    }
}
