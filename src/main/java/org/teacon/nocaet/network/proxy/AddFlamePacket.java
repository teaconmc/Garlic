package org.teacon.nocaet.network.proxy;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record AddFlamePacket(UUID uuid, ResourceLocation rl, Component item) {

    public AddFlamePacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readResourceLocation(), buf.readComponent());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeResourceLocation(rl);
        buf.writeComponent(item);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
    }
}
