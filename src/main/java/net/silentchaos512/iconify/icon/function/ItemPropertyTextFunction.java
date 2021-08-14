package net.silentchaos512.iconify.icon.function;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.api.icon.ITextFunctionSerializer;

import java.util.Optional;
import java.util.function.Function;

public class ItemPropertyTextFunction implements ITextFunction {
    private final ITextFunctionSerializer<?> serializer;
    private final Function<ItemStack, Optional<Component>> textFunction;

    public ItemPropertyTextFunction(ITextFunctionSerializer<?> serializer, Function<ItemStack, Optional<Component>> textFunction) {
        this.serializer = serializer;
        this.textFunction = textFunction;
    }

    @Override
    public Optional<Component> getText(ItemStack stack) {
        return textFunction.apply(stack);
    }

    @Override
    public ITextFunctionSerializer<?> getSerializer() {
        return serializer;
    }

    public static class Serializer extends AbstractTextFunctionSerializer<ItemPropertyTextFunction> {
        private final Function<ItemStack, Optional<Component>> textFunction;

        public Serializer(ResourceLocation name, Function<ItemStack, Optional<Component>> textFunction) {
            super(name);
            this.textFunction = textFunction;
        }

        @Override
        public ItemPropertyTextFunction deserialize(JsonObject json) {
            return new ItemPropertyTextFunction(this, textFunction);
        }

        @Override
        public JsonObject serialize(ItemPropertyTextFunction function) {
            return new JsonObject();
        }

        @Override
        public ItemPropertyTextFunction read(FriendlyByteBuf buffer) {
            return new ItemPropertyTextFunction(this, textFunction);
        }

        @Override
        public void write(FriendlyByteBuf buffer, ItemPropertyTextFunction function) {
        }
    }
}
