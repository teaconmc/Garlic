package org.teacon.nocaet.client.particle;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;
import org.teacon.nocaet.client.GarlicShaders;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LeavesParticle extends TextureSheetParticle {

    private static final int LIFETIME = 400;
    private static final float HALF_LIFE = LIFETIME / 2F;

    protected LeavesParticle(ClientLevel p_108328_, double p_108329_, double p_108330_, double p_108331_, double p_108332_, double p_108333_, double p_108334_) {
        super(p_108328_, p_108329_, p_108330_, p_108331_, p_108332_, p_108333_, p_108334_);
        this.gravity = 0.008F;
        this.lifetime = LIFETIME;
        if (GarlicShaders.getProgress() < Math.random()) {
            // FFCC00
            this.gCol = (float) 0xCC / (float) 0xFF;
        } else {
            // FF2F00
            this.gCol = (float) 0x2F / (float) 0xFF;
        }
        this.bCol = 0F;
        this.quadSize *= 0.5F;
        this.hasPhysics = false;
    }

    @Override
    public float getQuadSize(float pScaleFactor) {
        return (float) (this.quadSize * Math.tanh(5F * (1F - Math.abs((this.age - HALF_LIFE) / HALF_LIFE))));
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        return 0xF000F0;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            var particle = new LeavesParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            particle.pickSprite(this.sprite);
            return particle;
        }
    }
}
