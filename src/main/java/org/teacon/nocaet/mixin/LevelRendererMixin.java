package org.teacon.nocaet.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teacon.nocaet.client.GarlicRenderTypes;
import org.teacon.nocaet.client.GarlicShaders;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow
    protected abstract void renderChunkLayer(RenderType pRenderType, PoseStack pPoseStack, double pCamX, double pCamY, double pCamZ, Matrix4f pProjectionMatrix);

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderChunkLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLcom/mojang/math/Matrix4f;)V", ordinal = 0))
    private void drawLayers(PoseStack pPoseStack, float pPartialTick, long pFinishNanoTime, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pProjectionMatrix, CallbackInfo ci) {
        var pos = pCamera.getPosition();
        this.renderChunkLayer(GarlicRenderTypes.SOLID, pPoseStack, pos.x(), pos.y(), pos.z(), pProjectionMatrix);
        this.renderChunkLayer(GarlicRenderTypes.CUTOUT, pPoseStack, pos.x(), pos.y(), pos.z(), pProjectionMatrix);
    }

    @ModifyArg(method = "renderChunkLayer", index = 0, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/renderer/ShaderInstance;)V"))
    private ShaderInstance setProgress(ShaderInstance instance) {
        var progress = instance.getUniform("nocaetProgress");
        if (progress != null) {
            progress.set(GarlicShaders.getProgress());
        }
        return instance;
    }
}
