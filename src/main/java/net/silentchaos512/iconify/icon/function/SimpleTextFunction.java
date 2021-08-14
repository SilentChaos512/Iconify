package net.silentchaos512.iconify.icon.function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.silentchaos512.iconify.Iconify;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.api.icon.ITextFunctionSerializer;

import java.util.Optional;

public class SimpleTextFunction implements ITextFunction {
    private final Component text;

    public SimpleTextFunction(Component text) {
        this.text = text;
    }

    @Override
    public Optional<Component> getText(ItemStack stack) {
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
            return new SimpleTextFunction(Component.Serializer.fromJson(textJson));
        }

        @Override
        public JsonObject serialize(SimpleTextFunction function) {
            JsonObject json = new JsonObject();
            json.add("value", Component.Serializer.toJsonTree(function.text));
            return json;
        }

        @Override
        public SimpleTextFunction read(FriendlyByteBuf buffer) {
            return new SimpleTextFunction(buffer.readComponent());
        }

        @Override
        public void write(FriendlyByteBuf buffer, SimpleTextFunction function) {
            buffer.writeComponent(function.text);
        }
    }
}
