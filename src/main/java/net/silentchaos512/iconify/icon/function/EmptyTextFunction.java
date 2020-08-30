package net.silentchaos512.iconify.icon.function;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.api.icon.ITextFunctionSerializer;

import java.util.Optional;

public class EmptyTextFunction implements ITextFunction {
    public static final EmptyTextFunction INSTANCE = new EmptyTextFunction();

    @Override
    public Optional<ITextComponent> getText(ItemStack stack) {
        return Optional.empty();
    }

    @Override
    public ITextFunctionSerializer<?> getSerializer() {
        return IconFunctions.EMPTY;
    }
}
