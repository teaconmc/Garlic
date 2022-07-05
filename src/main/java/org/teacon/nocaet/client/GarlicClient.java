package org.teacon.nocaet.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.teacon.nocaet.GarlicRegistry;

import java.io.IOException;

public class GarlicClient {

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GarlicClient::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GarlicClient::blockColor);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GarlicClient::registerShader);
        MinecraftForge.EVENT_BUS.addListener(GarlicClient::addTooltip);
    }

    private static void registerShader(RegisterShadersEvent event) {
        try {
            GarlicShaders.register(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        for (RegistryObject<Block> object : GarlicRegistry.BLOCKS.getEntries()) {
            if (object.get().getRegistryName().getPath().contains("leaves")) {
                ItemBlockRenderTypes.setRenderLayer(object.get(), GarlicRenderTypes.CUTOUT);
            } else {
                ItemBlockRenderTypes.setRenderLayer(object.get(), GarlicRenderTypes.SOLID);
            }
        }
    }

    private static void blockColor(ColorHandlerEvent.Item event) {
        for (RegistryObject<Block> object : GarlicRegistry.BLOCKS.getEntries()) {
            var path = object.get().getRegistryName().getPath();
            if (path.contains("_leaves")) {
                var color = path.substring(0, path.indexOf("_leaves"));
                var dyeColor = DyeColor.byName(color, DyeColor.WHITE);
                event.getBlockColors().register((state, level, pos, tintIndex) -> dyeColor.getMaterialColor().col, object.get());
                event.getItemColors().register((stack, tintIndex) -> dyeColor.getMaterialColor().col, object.get().asItem());
            }
        }
    }

    private static void addTooltip(ItemTooltipEvent event) {
        var stack = event.getItemStack();
        if (GarlicRegistry.isFlameItem(stack.getItem())) {
            event.getToolTip().add(new TextComponent(""));
            event.getToolTip().add(new TranslatableComponent("nocaet.flame.tooltip"));
        }
    }
}
