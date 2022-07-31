package org.teacon.nocaet;

import net.minecraft.Util;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
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
            player.inventoryMenu.addSlotListener(new FlameListener(player));
            player.getCapability(GarlicCapability.flames()).ifPresent(it ->
                GarlicChannel.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new SyncFlamesPacket(it.getGranted()))
            );
            if (!player.getPersistentData().contains("NocaetScroll", Tag.TAG_BYTE)) {
                player.getPersistentData().putBoolean("NocaetScroll", true);
                player.getInventory().add(new ItemStack(GarlicRegistry.SCROLL_ITEM.get()));
            }
        }
    }

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        var tag = event.getOriginal().getPersistentData().get("NocaetScroll");
        if (tag != null) {
            event.getPlayer().getPersistentData().put("NocaetScroll", tag);
        }
    }

    private static void grantAndBroadcast(ServerPlayer player, FlameAdvancement advancement, ItemStack stack) {
        if (advancement.add(stack.getItem().getRegistryName())) {
            var text = new TranslatableComponent("nocaet.flame.grant", player.getDisplayName(), stack.getDisplayName());
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(text, ChatType.SYSTEM, Util.NIL_UUID);
            GarlicChannel.sendProxy(PacketDistributor.PLAYER.with(() -> player),
                new AddFlamePacket(player.getUUID(), stack.getItem().getRegistryName(), stack.getDisplayName()));
            GarlicChannel.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new SyncFlamesPacket(advancement.getGranted()));
        }
    }

    private record FlameListener(ServerPlayer player) implements ContainerListener {

        @Override
        public void slotChanged(AbstractContainerMenu pContainerToSend, int pSlotInd, ItemStack pStack) {
            if (pContainerToSend == player.inventoryMenu && !(pContainerToSend.getSlot(pSlotInd) instanceof ResultSlot)
                && player.gameMode.isSurvival()) {
                if (pStack.is(GarlicRegistry.FLAME_TAG)) {
                    var server = ServerLifecycleHooks.getCurrentServer();
                    server.tell(new TickTask(server.getTickCount(), () -> {
                        if (player.isAddedToWorld()) {
                            var slot = player.inventoryMenu.getSlot(pSlotInd);
                            if (slot.getItem().is(GarlicRegistry.FLAME_TAG)) {
                                var stack = slot.getItem().copy();
                                stack.getCapability(GarlicCapability.bind())
                                    .filter(it -> it.bindTo(player))
                                    .flatMap(it -> player.getCapability(GarlicCapability.flames()).resolve())
                                    .ifPresent(it -> {
                                        grantAndBroadcast(player, it, stack);
                                        slot.set(stack);
                                    });
                            }
                        }
                    }));
                }
            }
        }

        @Override
        public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
        }
    }
}
