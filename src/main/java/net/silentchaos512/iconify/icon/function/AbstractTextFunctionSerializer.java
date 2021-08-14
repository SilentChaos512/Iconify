package net.silentchaos512.iconify.icon.function;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.api.icon.ITextFunctionSerializer;

import java.util.function.Supplier;

public abstract class AbstractTextFunctionSerializer<T extends ITextFunction> implements ITextFunctionSerializer<T> {
    private final ResourceLocation name;

    @SuppressWarnings("WeakerAccess")
    public AbstractTextFunctionSerializer(ResourceLocation name) {
        this.name = name;
    }

    public static <T extends ITextFunction> AbstractTextFunctionSerializer<T> basic(ResourceLocation serializerId, Supplier<T> factory) {
        return new Basic<>(serializerId, factory);
    }

    @Override
    public ResourceLocation getName() {
        return this.name;
    }

    public static class Basic<T extends ITextFunction> extends AbstractTextFunctionSerializer<T> {
        private final Supplier<T> factory;

        public Basic(ResourceLocation serializerId, Supplier<T> factory) {
            super(serializerId);
            this.factory = factory;
        }

        @Override
        public T deserialize(JsonObject json) {
            return this.factory.get();
        }

        @Override
        public JsonObject serialize(T function) {
            return new JsonObject();
        }

        @Override
        public T read(FriendlyByteBuf buffer) {
            return this.factory.get();
        }

        @Override
        public void write(FriendlyByteBuf buffer, T function) {
            // NO-OP
        }
    }
}
