package org.teacon.nocaet;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import org.teacon.nocaet.network.GarlicChannel;
import org.teacon.nocaet.network.capability.FlameAdvancement;
import org.teacon.nocaet.network.capability.GarlicCapability;
import org.teacon.nocaet.network.play.SyncFlamesPacket;
import org.teacon.nocaet.network.proxy.AddFlamePacket;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GarlicEvents {

    @SubscribeEvent
    public void playerJoin(EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            player.inventoryMenu.addSlotListener(new ContainerListener() {

                @Override
                public void slotChanged(AbstractContainerMenu pContainerToSend, int pSlotInd, ItemStack pStack) {
                    if (pContainerToSend == player.inventoryMenu && pContainerToSend.getSlot(pSlotInd) instanceof ResultSlot) {
                        if (pStack.is(GarlicRegistry.FLAME_TAG)) {
                            pStack.getCapability(GarlicCapability.bind())
                                .filter(it -> it.bindTo(player))
                                .flatMap(it -> player.getCapability(GarlicCapability.flames()).resolve())
                                .ifPresent(it -> grantAndBroadcast(player, it, pStack));
                        }
                    }
                }

                @Override
                public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
                }
            });
            player.getCapability(GarlicCapability.flames()).ifPresent(it ->
                GarlicChannel.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new SyncFlamesPacket(it.getGranted()))
            );
        }
    }

    private void grantAndBroadcast(ServerPlayer player, FlameAdvancement advancement, ItemStack stack) {
        if (advancement.add(stack.getItem().getRegistryName())) {
            GarlicChannel.getProxyChannel().send(PacketDistributor.PLAYER.with(() -> player),
                new AddFlamePacket(player.getUUID(), stack.getItem().getRegistryName(), stack.getDisplayName()));
            GarlicChannel.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new SyncFlamesPacket(advancement.getGranted()));
        }
    }
}
