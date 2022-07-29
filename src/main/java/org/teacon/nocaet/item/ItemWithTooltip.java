package org.teacon.nocaet.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.teacon.nocaet.GarlicRegistry;
import org.teacon.nocaet.client.GarlicClient;

import java.util.List;

public class ItemWithTooltip extends Item {
    public ItemWithTooltip(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (!pStack.is(GarlicRegistry.FLAME_TAG)) {
            var id = pStack.getDescriptionId() + ".tooltip";
            pTooltipComponents.addAll(GarlicClient.translatableText(id));
            pTooltipComponents.add(TextComponent.EMPTY);
            pTooltipComponents.add(new TranslatableComponent("nocaet.flame.tooltip"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
