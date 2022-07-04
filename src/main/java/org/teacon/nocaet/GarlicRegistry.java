package org.teacon.nocaet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.teacon.nocaet.block.TransparentLeavesBlock;
import org.teacon.nocaet.block.TransparentLogBlock;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class GarlicRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GarlicMod.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GarlicMod.MODID);

    public static final CreativeModeTab TAB = new CreativeModeTab(GarlicMod.MODID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(ITEMS.getEntries().iterator().next().get());
        }
    };

    private static <T extends Block> void itemBlock(String name, Supplier<T> block) {
        var object = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(object.get(), new Item.Properties().tab(TAB)));
    }

    static {
        for (var block : List.of("log", "log_strip"/*, "branch", "root"*/)) {
            itemBlock(block, () -> new TransparentLogBlock(
                BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)
            ));
            itemBlock(block + "_shadow", () -> new RotatedPillarBlock(
                BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)
            ));
            itemBlock(block + "_light", () -> new TransparentLogBlock(
                BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)
                    .lightLevel(s -> 15).emissiveRendering((a, b, c) -> true)
            ));
        }
        for (var color : DyeColor.values()) {
            if (color.getId() < 16) {
                var name = color.getName() + "_" + "leaves";
                itemBlock(name, () -> new TransparentLeavesBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).sound(SoundType.GRASS).noOcclusion()
                ));
                itemBlock(name + "_shadow", () -> new Block(
                    BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).sound(SoundType.GRASS).noOcclusion()
                ));
                itemBlock(name + "_light", () -> new TransparentLeavesBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).sound(SoundType.GRASS).noOcclusion()
                        .lightLevel(s -> 15).emissiveRendering((a, b, c) -> true)
                ));
            }
        }
    }

    private static final Set<Item> FLAMES = new HashSet<>();

    private static void registerFlames(InterModProcessEvent event) {
        event.getIMCStream("register_flame"::equals)
            .map(it -> it.messageSupplier().get())
            .filter(ResourceLocation.class::isInstance)
            .map(ResourceLocation.class::cast)
            .map(ForgeRegistries.ITEMS::getValue)
            .filter(Objects::nonNull)
            .forEach(FLAMES::add);
    }

    public static Set<Item> getFlames() {
        return Collections.unmodifiableSet(FLAMES);
    }

    public static boolean isFlameItem(Item item) {
        return FLAMES.contains(item);
    }

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GarlicRegistry::registerFlames);
    }
}
