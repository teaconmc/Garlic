package org.teacon.nocaet.network.play;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.teacon.nocaet.client.GarlicClient;
import org.teacon.nocaet.network.capability.GarlicCapability;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public record SyncFlamesPacket(int[] items) {

    public SyncFlamesPacket(FriendlyByteBuf buf) {
        this(buf.readVarIntArray());
    }

    public SyncFlamesPacket(Collection<ResourceLocation> rls) {
        this(rls.stream().mapToInt(it -> Registry.ITEM.getId(ForgeRegistries.ITEMS.getValue(it))).toArray());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarIntArray(this.items);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                player.getCapability(GarlicCapability.flames()).ifPresent(adv -> {
                    //noinspection ConstantConditions
                    var list = Arrays.stream(items).mapToObj(it -> Registry.ITEM.byId(it).getRegistryName()).toList();
                    adv.getGranted().clear();
                    adv.getGranted().addAll(list);
                    GarlicClient.refreshClues();
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
