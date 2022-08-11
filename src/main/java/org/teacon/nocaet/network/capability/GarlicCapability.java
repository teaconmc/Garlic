package org.teacon.nocaet.network.capability;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.teacon.nocaet.GarlicRegistry;

public class GarlicCapability {

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GarlicCapability::register);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, GarlicCapability::attachPlayer);
        MinecraftForge.EVENT_BUS.addListener(GarlicCapability::clonePlayer);
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, GarlicCapability::attackItem);
    }

    private static void register(RegisterCapabilitiesEvent event) {
        event.register(FlameAdvancement.class);
        event.register(Bind.class);
    }

    private static void attachPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(FlameAdvancement.ID, new FlameAdvancement.Provider());
        }
    }

    private static void clonePlayer(PlayerEvent.Clone event) {
        try {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(flames()).ifPresent(old -> {
                event.getEntity().getCapability(flames()).ifPresent(it -> {
                    it.getGranted().clear();
                    it.getGranted().addAll(old.getGranted());
                });
            });
        } finally {
            event.getOriginal().invalidateCaps();
        }
    }

    private static void attackItem(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().is(GarlicRegistry.FLAME_TAG)) {
            event.addCapability(Bind.ID, new Bind.Provider());
        }
    }

    public static Capability<FlameAdvancement> flames() {
        return FlameAdvancement.Provider.CAPABILITY;
    }

    public static Capability<Bind> bind() {
        return Bind.Provider.CAPABILITY;
    }
}
