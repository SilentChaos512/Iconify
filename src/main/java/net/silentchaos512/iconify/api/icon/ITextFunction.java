package net.silentchaos512.iconify.api.icon;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;

public interface ITextFunction {
    Optional<ITextComponent> getText(ItemStack stack);

    ITextFunctionSerializer<?> getSerializer();
}
