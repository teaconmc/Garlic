package org.teacon.nocaet.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public record SyncProgressPacket(double progress) {

    public SyncProgressPacket(FriendlyByteBuf buf) {
        this(buf.readDouble());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(this.progress);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        GarlicChannel.getChannel().send(PacketDistributor.ALL.noArg(), new SetProgressPacket(progress));
        ctx.get().setPacketHandled(true);
    }
}
