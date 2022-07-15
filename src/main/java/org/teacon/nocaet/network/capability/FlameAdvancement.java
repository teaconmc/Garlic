package org.teacon.nocaet.network.capability;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.teacon.nocaet.GarlicMod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FlameAdvancement {

    static final ResourceLocation ID = new ResourceLocation(GarlicMod.MODID, "progress");
    private final Set<ResourceLocation> list;

    public FlameAdvancement() {
        this(new ArrayList<>());
    }

    public FlameAdvancement(Collection<ResourceLocation> list) {
        this.list = new HashSet<>(list);
    }

    public Set<ResourceLocation> getGranted() {
        return list;
    }

    public boolean add(ResourceLocation rl) {
        if (list.contains(rl)) {
            return false;
        } else {
            return list.add(rl);
        }
    }

    public boolean contains(ResourceLocation rl) {
        return this.list.contains(rl);
    }

    static class Provider implements ICapabilityProvider {

        static final Capability<FlameAdvancement> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

        private final FlameAdvancement advancement = new FlameAdvancement();

        private final LazyOptional<FlameAdvancement> instance = LazyOptional.of(() -> advancement);

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return CAPABILITY.orEmpty(cap, instance);
        }
    }
}
