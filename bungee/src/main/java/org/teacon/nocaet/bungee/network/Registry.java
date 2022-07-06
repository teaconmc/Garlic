package org.teacon.nocaet.bungee.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.teacon.nocaet.bungee.network.packet.AddFlame;
import org.teacon.nocaet.bungee.network.packet.SetFlames;
import org.teacon.nocaet.bungee.network.packet.SyncProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Registry {

    private static final Registry INSTANCE = new Registry();

    public static Registry instance() {
        return INSTANCE;
    }

    private final List<Function<ByteBuf, ? extends Packet>> decoders = new ArrayList<>();

    private final Map<Class<? extends Packet>, Integer> ids = new HashMap<>();

    public Registry() {
        register(SyncProgress.class, SyncProgress::new);
        register(AddFlame.class, AddFlame::new);
        register(SetFlames.class, SetFlames::new);
    }

    private <T extends Packet> void register(Class<T> cl, Function<ByteBuf, T> decoder) {
        var id = decoders.size();
        decoders.add(decoder);
        ids.put(cl, id);
    }

    public Packet decode(byte[] payload) {
        var buf = Unpooled.wrappedBuffer(payload);
        var id = buf.readByte();
        return decoders.get(id).apply(buf);
    }

    private byte[] encode(Packet packet) {
        var id = ids.get(packet.getClass());
        var buf = Unpooled.buffer();
        buf.writeByte(id);
        packet.write(buf);
        var payload = new byte[buf.readableBytes()];
        buf.readBytes(payload);
        return payload;
    }

    public void send(Packet packet, ServerInfo server) {
        server.sendData("nocaet:proxy", encode(packet));
    }

    public void sendToAll(Packet packet) {
        var bytes = encode(packet);
        for (ServerInfo info : ProxyServer.getInstance().getServers().values()) {
            info.sendData("nocaet:proxy", bytes);
        }
    }
}
