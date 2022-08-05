package org.teacon.nocaet;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.teacon.nocaet.block.TransparentLeavesBlock;
import org.teacon.nocaet.block.TransparentLogBlock;
import org.teacon.nocaet.item.ItemWithTooltip;

import java.util.List;
import java.util.function.Supplier;

public class GarlicRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GarlicMod.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GarlicMod.MODID);
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, GarlicMod.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, GarlicMod.MODID);
    public static final TagKey<Item> FLAME_TAG = ItemTags.create(new ResourceLocation(GarlicMod.MODID, "flames"));
    public static final TagKey<Block> LEAVES_TAG = BlockTags.create(new ResourceLocation(GarlicMod.MODID, "leaves"));

    public static final CreativeModeTab TAB = new CreativeModeTab(GarlicMod.MODID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(ITEMS.getEntries().iterator().next().get());
        }
    };

    public static RegistryObject<Item> SCROLL_ITEM;
    public static RegistryObject<SimpleParticleType> LEAVES_PARTICLE;

    private static <T extends Block> void itemBlock(String name, Supplier<T> block) {
        var object = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(object.get(), new Item.Properties().tab(TAB)));
    }

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BIOMES.register(FMLJavaModLoadingContext.get().getModEventBus());
        PARTICLE.register(FMLJavaModLoadingContext.get().getModEventBus());
        registerBuiltin();
        registerFlames();
    }

    private static void registerBuiltin() {
        for (var block : List.of("log", "log_strip")) {
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
        itemBlock("leaves", () -> new TransparentLeavesBlock(
            BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).sound(SoundType.GRASS).noOcclusion()
                .isValidSpawn((a, b, c, d) -> false)
        ));
        itemBlock("leaves_large", () -> new TransparentLeavesBlock(
            BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).sound(SoundType.GRASS).noOcclusion()
                .isValidSpawn((a, b, c, d) -> false)
        ));
        itemBlock("leaves_very_large", () -> new TransparentLeavesBlock(
            BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).sound(SoundType.GRASS).noOcclusion()
                .isValidSpawn((a, b, c, d) -> false)
        ));
        itemBlock("leaves_shadow", () -> new Block(
            BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).sound(SoundType.GRASS).noOcclusion()
                .isValidSpawn((a, b, c, d) -> false)
        ));
        itemBlock("leaves_light", () -> new TransparentLeavesBlock(
            BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).sound(SoundType.GRASS).noOcclusion()
                .isValidSpawn((a, b, c, d) -> false).lightLevel(s -> 15).emissiveRendering((a, b, c) -> true)
        ));
        SCROLL_ITEM = ITEMS.register("elder_scroll", () -> new Item(new Item.Properties().tab(TAB)));
        BIOMES.register("ash_lake", () -> new Biome.BiomeBuilder()
            .precipitation(Biome.Precipitation.NONE)
            .biomeCategory(Biome.BiomeCategory.OCEAN)
            .temperature(0.5F)
            .downfall(0.5F)
            .specialEffects(new BiomeSpecialEffects.Builder()
                .waterColor(0x45adf2)
                .waterFogColor(0x041633)
                .fogColor(0x4f5ad1)
                .skyColor(0x4f5ad1)
                .build())
            .mobSpawnSettings(new MobSpawnSettings.Builder().build())
            .generationSettings(new BiomeGenerationSettings.Builder().build())
            .build());
        LEAVES_PARTICLE = PARTICLE.register("leaves", () -> new SimpleParticleType(false));
    }

    private static void registerFlames() {
        ITEMS.register("fading_flame", () -> new Item(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
        ITEMS.register("blood_flame", () -> new Item(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
        ITEMS.register("atziluth", () -> new Item(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
        ITEMS.register("spark_of_cosmo", () -> new Item(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
        ITEMS.register("spiritual_glow", () -> new Item(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
        ITEMS.register("bz_flame", () -> new Item(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
        ITEMS.register("quantum_fruit", () -> new ItemWithTooltip(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
        ITEMS.register("quantum_skewer", () -> new ItemWithTooltip(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
        ITEMS.register("blast_burst_quantum_skewer", () -> new Item(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
        ITEMS.register("fire_in_time", () -> new Item(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
        ITEMS.register("floo_flame", () -> new Item(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
        ITEMS.register("wooden_bar_with_spark", () -> new Item(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
        ITEMS.register("mysterious_meatballs", () -> new Item(new Item.Properties().tab(TAB).rarity(Rarity.UNCOMMON)));
    }
}
