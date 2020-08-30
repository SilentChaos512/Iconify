package net.silentchaos512.iconify.api.icon;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface ITextFunctionSerializer<T extends ITextFunction> {
    T deserialize(JsonObject json);

    JsonObject serialize(T function);

    T read(PacketBuffer buffer);

    void write(PacketBuffer buffer, T function);

    ResourceLocation getName();
}
