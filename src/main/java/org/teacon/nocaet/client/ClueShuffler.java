package org.teacon.nocaet.client;

import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.teacon.nocaet.GarlicRegistry;
import org.teacon.nocaet.network.capability.GarlicCapability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ClueShuffler {

    private final List<Item> queue = new ArrayList<>();
    private int index;

    public Optional<String> get() {
        if (this.queue.isEmpty()) {
            return Optional.empty();
        }
        var item = this.queue.get(this.index);
        return Optional.of(item.getDescriptionId() + ".clue");
    }

    public void next() {
        this.index++;
        if (this.index >= this.queue.size()) {
            this.shuffle();
        }
    }

    private void shuffle() {
        this.index = 0;
        this.queue.clear();
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var cap = player.getCapability(GarlicCapability.flames()).resolve().orElseThrow();
        var lang = Language.getInstance();
        for (var item : ForgeRegistries.ITEMS.tags().getTag(GarlicRegistry.FLAME_TAG)) {
            var key = item.getDescriptionId() + ".clue";
            if (lang.has(key) && !cap.contains(item.getRegistryName())) {
                this.queue.add(item);
            }
        }
        Collections.shuffle(this.queue);
    }
}
