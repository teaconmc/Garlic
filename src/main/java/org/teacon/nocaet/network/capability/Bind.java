package org.teacon.nocaet.network.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.teacon.nocaet.GarlicMod;

import java.time.Instant;
import java.util.UUID;

public class Bind {

    static final ResourceLocation ID = new ResourceLocation(GarlicMod.MODID, "progress");

    private UUID owner;
    private Instant instant;

    public void read(CompoundTag tag) {
        if (tag.contains("Owner")) {
            this.owner = tag.getUUID("Owner");
            this.instant = Instant.ofEpochMilli(tag.getLong("Date"));
        }
    }

    public void write(CompoundTag tag) {
        if (this.owner != null) {
            tag.putUUID("Owner", this.owner);
            tag.putLong("Date", this.instant.toEpochMilli());
        }
    }

    public UUID getOwner() {
        return owner;
    }

    public Instant getInstant() {
        return instant;
    }

    public boolean isOwner(ServerPlayer player) {
        return this.owner != null && player.getUUID().equals(this.owner);
    }

    public boolean bindTo(ServerPlayer player) {
        if (this.owner != null) {
            return false;
        } else {
            this.owner = player.getUUID();
            this.instant = Instant.now();
            return true;
        }
    }

    static class Provider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {

        static final Capability<Bind> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

        private final Bind bind = new Bind();
        private final LazyOptional<Bind> capability = LazyOptional.of(() -> bind);

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return CAPABILITY.orEmpty(cap, this.capability);
        }

        @Override
        public CompoundTag serializeNBT() {
            var tag = new CompoundTag();
            this.bind.write(tag);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.bind.read(nbt);
        }
    }
}
