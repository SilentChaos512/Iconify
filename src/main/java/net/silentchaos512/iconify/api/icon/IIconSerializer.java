package net.silentchaos512.iconify.api.icon;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface IIconSerializer<T extends IIcon> {
    T deserialize(ResourceLocation id, JsonObject json);

    T read(ResourceLocation id, PacketBuffer buffer);

    void write(PacketBuffer buffer, T icon);

    ResourceLocation getName();
}
