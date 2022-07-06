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
import java.util.List;

public class FlameAdvancement {

    static final ResourceLocation ID = new ResourceLocation(GarlicMod.MODID, "progress");
    private final List<ResourceLocation> list;

    public FlameAdvancement() {
        this(new ArrayList<>());
    }

    public FlameAdvancement(List<ResourceLocation> list) {
        this.list = list;
    }

    public List<ResourceLocation> getList() {
        return list;
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
