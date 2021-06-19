package net.silentchaos512.iconify.icon.function;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.api.icon.ITextFunctionSerializer;

import java.util.Optional;
import java.util.function.Function;

public class ItemPropertyTextFunction implements ITextFunction {
    private final ITextFunctionSerializer<?> serializer;
    private final Function<ItemStack, Optional<ITextComponent>> textFunction;

    public ItemPropertyTextFunction(ITextFunctionSerializer<?> serializer, Function<ItemStack, Optional<ITextComponent>> textFunction) {
        this.serializer = serializer;
        this.textFunction = textFunction;
    }

    @Override
    public Optional<ITextComponent> getText(ItemStack stack) {
        return textFunction.apply(stack);
    }

    @Override
    public ITextFunctionSerializer<?> getSerializer() {
        return serializer;
    }

    public static class Serializer extends AbstractTextFunctionSerializer<ItemPropertyTextFunction> {
        private final Function<ItemStack, Optional<ITextComponent>> textFunction;

        public Serializer(ResourceLocation name, Function<ItemStack, Optional<ITextComponent>> textFunction) {
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
        public ItemPropertyTextFunction read(PacketBuffer buffer) {
            return new ItemPropertyTextFunction(this, textFunction);
        }

        @Override
        public void write(PacketBuffer buffer, ItemPropertyTextFunction function) {
        }
    }
}
