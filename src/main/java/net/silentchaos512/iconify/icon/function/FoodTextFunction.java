package net.silentchaos512.iconify.icon.function;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.api.icon.ITextFunctionSerializer;

import java.util.Optional;

public class FoodTextFunction implements ITextFunction {
    @Override
    public Optional<Component> getText(ItemStack stack) {
        if (stack.isEdible() && stack.getItem().getFoodProperties() != null) {
            FoodProperties food = stack.getItem().getFoodProperties();
            int healing = food.getNutrition();
            int saturation = Math.round(100 * food.getSaturationModifier());
            return Optional.of(new TextComponent(String.format("%d/%d%%", healing, saturation)));
        }
        return Optional.empty();
    }

    @Override
    public ITextFunctionSerializer<?> getSerializer() {
        return IconFunctions.FOOD;
    }
}
