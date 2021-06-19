package net.silentchaos512.iconify.icon.function;

import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.api.icon.ITextFunctionSerializer;

import java.util.Optional;

public class FoodTextFunction implements ITextFunction {
    @Override
    public Optional<ITextComponent> getText(ItemStack stack) {
        if (stack.isFood() && stack.getItem().getFood() != null) {
            Food food = stack.getItem().getFood();
            int healing = food.getHealing();
            int saturation = Math.round(100 * food.getSaturation());
            return Optional.of(new StringTextComponent(String.format("%d/%d%%", healing, saturation)));
        }
        return Optional.empty();
    }

    @Override
    public ITextFunctionSerializer<?> getSerializer() {
        return IconFunctions.FOOD;
    }
}
