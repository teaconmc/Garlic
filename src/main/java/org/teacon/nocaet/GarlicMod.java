package org.teacon.nocaet;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.teacon.nocaet.client.GarlicClient;
import org.teacon.nocaet.command.GarlicCommands;
import org.teacon.nocaet.data.GarlicData;
import org.teacon.nocaet.network.GarlicChannel;

@Mod(GarlicMod.MODID)
public class GarlicMod {

    public static final String MODID = "nocaet";

    public static final Logger LOGGER = LogUtils.getLogger();

    public GarlicMod() {
        GarlicRegistry.register();
        GarlicCommands.init();
        GarlicChannel.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GarlicData::register);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> GarlicClient::init);
    }
}