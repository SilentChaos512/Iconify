package net.silentchaos512.iconify.api.icon;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;

public interface IIcon {
    ResourceLocation getId();

    IIconSerializer<?> getSerializer();

    String getGroup();

    ResourceLocation getIconTexture();

    Optional<ITextComponent> getIconText(ItemStack stack);

    boolean test(ItemStack stack);
}
