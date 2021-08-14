package net.silentchaos512.iconify.api.icon;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public interface IIcon {
    ResourceLocation getId();

    IIconSerializer<?> getSerializer();

    String getGroup();

    ResourceLocation getIconTexture();

    Optional<Component> getIconText(ItemStack stack);

    boolean test(ItemStack stack);

    boolean isVisibleWhenTextEmpty();
}
