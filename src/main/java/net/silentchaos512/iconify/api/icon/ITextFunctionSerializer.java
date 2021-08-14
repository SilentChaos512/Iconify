package net.silentchaos512.iconify.api.icon;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface ITextFunctionSerializer<T extends ITextFunction> {
    T deserialize(JsonObject json);

    JsonObject serialize(T function);

    T read(FriendlyByteBuf buffer);

    void write(FriendlyByteBuf buffer, T function);

    ResourceLocation getName();
}
