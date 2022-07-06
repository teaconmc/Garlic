package org.teacon.nocaet.network.play;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.teacon.nocaet.client.GarlicShaders;

import java.util.function.Supplier;

public record SetProgressPacket(float progress) {

    public SetProgressPacket(FriendlyByteBuf buf) {
        this(buf.readFloat());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(this.progress);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        GarlicShaders.setProgress(progress);
        ctx.get().setPacketHandled(true);
    }
}
