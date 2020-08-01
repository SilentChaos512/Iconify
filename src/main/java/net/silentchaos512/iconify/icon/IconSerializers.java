package net.silentchaos512.iconify.icon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.iconify.Iconify;
import net.silentchaos512.iconify.api.icon.IIcon;
import net.silentchaos512.iconify.api.icon.IIconSerializer;
import net.silentchaos512.iconify.icon.type.ModIdIcon;
import net.silentchaos512.iconify.icon.type.SimpleIcon;
import net.silentchaos512.iconify.icon.type.TagIcon;
import net.silentchaos512.iconify.icon.type.ToolTypeIcon;

import java.util.HashMap;
import java.util.Map;

public final class IconSerializers {
    private static final Map<ResourceLocation, IIconSerializer<?>> REGISTRY = new HashMap<>();

    public static final IIconSerializer<SimpleIcon> SIMPLE = register(new SimpleIcon.Serializer<>(Iconify.getId("simple"), SimpleIcon::new));
    public static final IIconSerializer<ModIdIcon> MOD_ID = register(new ModIdIcon.Serializer(Iconify.getId("mod_id"), ModIdIcon::new));
    public static final IIconSerializer<TagIcon> TAG = register(new TagIcon.Serializer(Iconify.getId("tag"), TagIcon::new));
    public static final IIconSerializer<ToolTypeIcon> TOOL_TYPE = register(new ToolTypeIcon.Serializer(Iconify.getId("tool_type"), ToolTypeIcon::new));

    private IconSerializers() {}

    public static <S extends IIconSerializer<T>, T extends IIcon> S register(S serializer) {
        if (REGISTRY.containsKey(serializer.getName())) {
            throw new IllegalArgumentException("Duplicate icon serializer: " + serializer.getName());
        }
        Iconify.LOGGER.info("Registered icon serializer '{}'", serializer.getName());
        REGISTRY.put(serializer.getName(), serializer);
        return serializer;
    }

    public static IIcon deserialize(ResourceLocation id, JsonObject json) {
        String typeStr = JSONUtils.getString(json, "type");
        ResourceLocation type = Iconify.getIdWithDefaultNamespace(typeStr);
        Iconify.LOGGER.debug("deserialize '{}' (type {})", id, type);

        IIconSerializer<?> serializer = REGISTRY.get(type);
        if (serializer == null) {
            throw new JsonParseException("Invalid or unsupported icon type: " + type);
        }
        return serializer.deserialize(id, json);
    }

    public static IIcon read(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        ResourceLocation type = buffer.readResourceLocation();
        Iconify.LOGGER.debug("read '{}', (type {})", id, type);

        IIconSerializer<?> serializer = REGISTRY.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown icon serializer: " + type);
        }
        return serializer.read(id, buffer);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IIcon> void write(T icon, PacketBuffer buffer) {
        ResourceLocation id = icon.getId();
        ResourceLocation type = icon.getSerializer().getName();
        Iconify.LOGGER.debug("write '{}' (type {})", id, type);

        buffer.writeResourceLocation(id);
        buffer.writeResourceLocation(type);
        IIconSerializer<T> serializer = (IIconSerializer<T>) icon.getSerializer();
        serializer.write(buffer, icon);
    }
}
