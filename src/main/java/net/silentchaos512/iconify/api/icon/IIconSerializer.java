package net.silentchaos512.iconify.api.icon;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IIconSerializer<T extends IIcon> {
    T deserialize(ResourceLocation id, JsonObject json);

    T read(ResourceLocation id, FriendlyByteBuf buffer);

    void write(FriendlyByteBuf buffer, T icon);

    ResourceLocation getName();
}
