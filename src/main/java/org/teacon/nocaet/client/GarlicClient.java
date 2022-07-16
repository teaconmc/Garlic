package org.teacon.nocaet.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.teacon.nocaet.GarlicRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GarlicClient {

    private static final ClueShuffler CLUES = new ClueShuffler();
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor(t -> {
        var thread = new Thread(t);
        thread.setName("garlic-refresh");
        thread.setDaemon(true);
        return thread;
    });

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GarlicClient::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GarlicClient::blockColor);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GarlicClient::registerShader);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, GarlicClient::addTooltip);
        EXECUTOR.scheduleAtFixedRate(() -> Minecraft.getInstance().execute(CLUES::next), 10, 10, TimeUnit.MINUTES);
    }

    public static void refreshClues() {
        CLUES.shuffle();
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
                ItemBlockRenderTypes.setRenderLayer(object.get(), GarlicRenderTypes.GARLIC_CUTOUT);
            } else {
                ItemBlockRenderTypes.setRenderLayer(object.get(), GarlicRenderTypes.GARLIC_SOLID);
            }
        }
    }

    private static void blockColor(ColorHandlerEvent.Item event) {
        for (RegistryObject<Block> object : GarlicRegistry.BLOCKS.getEntries()) {
            var path = object.get().getRegistryName().getPath();
            if (path.contains("leaves")) {
                event.getItemColors().register((stack, tintIndex) -> DyeColor.YELLOW.getMaterialColor().col, object.get().asItem());
            }
        }
    }

    private static void addTooltip(ItemTooltipEvent event) {
        var stack = event.getItemStack();
        if (stack.is(GarlicRegistry.FLAME_TAG)) {
            var id = stack.getDescriptionId() + ".tooltip";
            event.getToolTip().addAll(translatableText(id));
            event.getToolTip().add(TextComponent.EMPTY);
            event.getToolTip().add(new TranslatableComponent("nocaet.flame.tooltip"));
        } else if (stack.is(GarlicRegistry.SCROLL_ITEM.get())) {
            var optional = CLUES.get();
            if (optional.isEmpty()) {
                event.getToolTip().addAll(translatableText("nocaet.scroll.tooltip.empty"));
            } else {
                event.getToolTip().addAll(translatableText("nocaet.scroll.tooltip.clue"));
                event.getToolTip().add(TextComponent.EMPTY);
                event.getToolTip().addAll(translatableText(optional.get()));
            }
            event.getToolTip().add(TextComponent.EMPTY);
            event.getToolTip().add(new TranslatableComponent("nocaet.flame.tooltip"));
        }
    }

    private static List<Component> translatableText(String key) {
        var lang = Language.getInstance();
        if (lang.has(key)) {
            var text = new ArrayList<Component>();
            for (var split : lang.getOrDefault(key).split("\n")) {
                text.add(new TextComponent(split));
            }
            return text;
        } else {
            return List.of();
        }
    }
}
