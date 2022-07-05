package org.teacon.nocaet.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import org.teacon.nocaet.GarlicMod;

import java.io.IOException;

public class GarlicShaders {

    private static volatile float progress;

    public static float getProgress() {
        return progress;
    }

    public static void setProgress(float progress) {
        GarlicShaders.progress = progress;
    }

    public static ShaderInstance SOLID;
    public static ShaderInstance CUTOUT;

    static final RenderStateShard.ShaderStateShard SOLID_STATE = new RenderStateShard.ShaderStateShard(() -> SOLID);
    static final RenderStateShard.ShaderStateShard CUTOUT_STATE = new RenderStateShard.ShaderStateShard(() -> CUTOUT);

    static void register(RegisterShadersEvent event) throws IOException {
        event.registerShader(SOLID = new ShaderInstance(event.getResourceManager(),
            new ResourceLocation(GarlicMod.MODID, "rendertype_solid"), DefaultVertexFormat.BLOCK), s -> {});
        event.registerShader(CUTOUT = new ShaderInstance(event.getResourceManager(),
            new ResourceLocation(GarlicMod.MODID, "rendertype_cutout"), DefaultVertexFormat.BLOCK), s -> {});
    }
}
