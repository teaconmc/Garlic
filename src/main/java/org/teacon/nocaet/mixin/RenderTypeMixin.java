package org.teacon.nocaet.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.nocaet.client.GarlicRenderTypes;

import java.util.List;

@Mixin(RenderType.class)
public class RenderTypeMixin {

    @Inject(method = "chunkBufferLayers", cancellable = true, at = @At("RETURN"))
    private static void addLayers(CallbackInfoReturnable<List<RenderType>> cir) {
        cir.setReturnValue(
            ImmutableList.<RenderType>builder()
                .addAll(cir.getReturnValue())
                .add(GarlicRenderTypes.SOLID, GarlicRenderTypes.CUTOUT)
                .build()
        );
    }
}
