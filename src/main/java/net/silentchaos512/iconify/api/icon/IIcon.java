package net.silentchaos512.iconify.api.icon;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public interface IIcon {
    ResourceLocation getId();

    IIconSerializer<?> getSerializer();

    String getGroup();

    ResourceLocation getIconTexture();

    ITextComponent getIconText();

    boolean test(ItemStack stack);
}
