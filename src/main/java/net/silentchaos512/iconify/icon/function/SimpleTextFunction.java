package net.silentchaos512.iconify.icon.function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.iconify.Iconify;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.api.icon.ITextFunctionSerializer;

import java.util.Optional;

public class SimpleTextFunction implements ITextFunction {
    private final ITextComponent text;

    public SimpleTextFunction(ITextComponent text) {
        this.text = text;
    }

    @Override
    public Optional<ITextComponent> getText(ItemStack stack) {
        return Optional.of(this.text);
    }

    @Override
    public ITextFunctionSerializer<?> getSerializer() {
        return IconFunctions.SIMPLE;
    }

    public static class Serializer extends AbstractTextFunctionSerializer<SimpleTextFunction>{
        Serializer() {
            super(Iconify.getId("simple"));
        }

        @Override
        public SimpleTextFunction deserialize(JsonObject json) {
            JsonElement textJson = json.has("value") ? json.get("value") : json;
            return new SimpleTextFunction(ITextComponent.Serializer.fromJson(textJson));
        }

        @Override
        public JsonObject serialize(SimpleTextFunction function) {
            JsonObject json = new JsonObject();
            json.add("value", ITextComponent.Serializer.toJsonTree(function.text));
            return json;
        }

        @Override
        public SimpleTextFunction read(PacketBuffer buffer) {
            return new SimpleTextFunction(buffer.readComponent());
        }

        @Override
        public void write(PacketBuffer buffer, SimpleTextFunction function) {
            buffer.writeComponent(function.text);
        }
    }
}
