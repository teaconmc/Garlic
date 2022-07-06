package org.teacon.nocaet.network.capability;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class GarlicCapability {

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GarlicCapability::register);
        MinecraftForge.EVENT_BUS.addListener(GarlicCapability::attach);
    }

    private static void register(RegisterCapabilitiesEvent event) {
        event.register(FlameAdvancement.class);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayer) {
            event.addCapability(FlameAdvancement.ID, new FlameAdvancement.Provider());
        }
    }

    public static Capability<FlameAdvancement> flames() {
        return FlameAdvancement.Provider.CAPABILITY;
    }
}
