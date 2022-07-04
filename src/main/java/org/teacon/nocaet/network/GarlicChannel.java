package org.teacon.nocaet.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.teacon.nocaet.GarlicMod;

import java.util.Optional;

public class GarlicChannel {

    private static SimpleChannel channel, proxyChannel;

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GarlicChannel::registerChannel);
    }

    private static void registerChannel(FMLCommonSetupEvent event) {
        channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(GarlicMod.MODID, "ch"), () -> "1", "1"::equals, "1"::equals);
        channel.registerMessage(0, SetProgressPacket.class, SetProgressPacket::write, SetProgressPacket::new,
            SetProgressPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        proxyChannel = NetworkRegistry.newSimpleChannel(new ResourceLocation(GarlicMod.MODID, "proxy"), () -> "1",
            NetworkRegistry.acceptMissingOr("1"::equals), NetworkRegistry.acceptMissingOr("1"::equals));
        proxyChannel.registerMessage(0, SyncProgressPacket.class, SyncProgressPacket::write, SyncProgressPacket::new,
            SyncProgressPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static SimpleChannel getChannel() {
        return channel;
    }

    public static SimpleChannel getProxyChannel() {
        return proxyChannel;
    }
}
