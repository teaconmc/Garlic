package org.teacon.nocaet.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.teacon.nocaet.GarlicRegistry;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class LeavesParticleHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && Minecraft.getInstance().level != null
            && Minecraft.getInstance().player != null) {
            var level = Minecraft.getInstance().level;
            var player = Minecraft.getInstance().player;
            var current = ThreadLocalRandom.current();
            var pos = new BlockPos.MutableBlockPos();
            for (int x = -2; x < 3; x++) {
                for (int z = -2; z < 3; z++) {
                    spawnParticles(level, level.getChunk(player.chunkPosition().x + x, player.chunkPosition().z + z), current, pos, player.position());
                }
            }
        }
    }

    private static void spawnParticles(ClientLevel level, ChunkAccess chunk, Random random, BlockPos.MutableBlockPos pos, Vec3 position) {
        int count = switch (Minecraft.getInstance().options.particles) {
            case ALL -> 8;
            case DECREASED -> 4;
            default -> 0;
        };
        for (int i = 0; i < count; i++) {
            pos.set(random.nextInt(16),
                random.nextInt(chunk.getMaxBuildHeight() - chunk.getMinBuildHeight()) + chunk.getMinBuildHeight(),
                random.nextInt(16));
            if (chunk.getBlockState(pos).is(GarlicRegistry.LEAVES_TAG)) {
                var y = position.y + random.nextInt(32) - 5;
                level.addParticle(GarlicRegistry.LEAVES_PARTICLE.get(), chunk.getPos().getBlockX(pos.getX()), y, chunk.getPos().getBlockZ(pos.getZ()), 0, 0, 0);
            }
        }
    }
}
