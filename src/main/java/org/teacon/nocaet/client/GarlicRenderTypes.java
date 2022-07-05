package org.teacon.nocaet.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

public class GarlicRenderTypes extends RenderType {

    private GarlicRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
        throw new AssertionError();
    }

    public static final RenderType SOLID = RenderType.create(
        "solid_nocaet", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152,
        true, false, RenderType.CompositeState.builder()
            .setLightmapState(LIGHTMAP).setShaderState(GarlicShaders.SOLID_STATE)
            .setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true)
    );

    public static final RenderType CUTOUT = RenderType.create(
        "cutout_nocaet", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152,
        true, false, RenderType.CompositeState.builder()
            .setLightmapState(NO_LIGHTMAP).setShaderState(GarlicShaders.CUTOUT_STATE)
            .setTextureState(BLOCK_SHEET).createCompositeState(true)
    );
}
