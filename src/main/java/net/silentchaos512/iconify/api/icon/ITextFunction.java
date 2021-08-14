package net.silentchaos512.iconify.api.icon;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public interface ITextFunction {
    Optional<Component> getText(ItemStack stack);

    ITextFunctionSerializer<?> getSerializer();
}
