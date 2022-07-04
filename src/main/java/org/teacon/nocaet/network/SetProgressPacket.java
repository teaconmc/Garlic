package org.teacon.nocaet.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetProgressPacket(double progress) {

    public SetProgressPacket(FriendlyByteBuf buf) {
        this(buf.readDouble());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(this.progress);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        // todo update progress
        ctx.get().setPacketHandled(true);
    }
}
