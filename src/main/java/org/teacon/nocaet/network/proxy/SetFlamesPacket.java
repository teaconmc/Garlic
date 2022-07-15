package org.teacon.nocaet.network.proxy;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.teacon.nocaet.network.GarlicChannel;
import org.teacon.nocaet.network.capability.GarlicCapability;
import org.teacon.nocaet.network.play.SyncFlamesPacket;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public record SetFlamesPacket(UUID uuid, List<ResourceLocation> flames) {

    public SetFlamesPacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readList(FriendlyByteBuf::readResourceLocation));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeCollection(flames, FriendlyByteBuf::writeResourceLocation);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
            if (player != null) {
                player.getCapability(GarlicCapability.flames()).ifPresent(adv -> {
                    adv.getGranted().clear();
                    adv.getGranted().addAll(flames);
                    GarlicChannel.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new SyncFlamesPacket(adv.getGranted()));
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
