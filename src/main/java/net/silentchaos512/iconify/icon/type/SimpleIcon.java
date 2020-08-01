package net.silentchaos512.iconify.icon.type;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.iconify.api.icon.IIcon;
import net.silentchaos512.iconify.api.icon.IIconSerializer;
import net.silentchaos512.iconify.icon.IconSerializers;

import java.util.function.Function;

public class SimpleIcon implements IIcon {
    private final ResourceLocation iconId;
    String group = "";
    ResourceLocation texture;
    ITextComponent text;

    public SimpleIcon(ResourceLocation iconId) {
        this.iconId = iconId;
    }

    @Override
    public ResourceLocation getId() {
        return iconId;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public ResourceLocation getIconTexture() {
        return texture;
    }

    @Override
    public ITextComponent getIconText() {
        return text;
    }

    @Override
    public IIconSerializer<?> getSerializer() {
        return IconSerializers.SIMPLE;
    }

    @Override
    public boolean test(ItemStack stack) {
        return true;
    }

    public static class Serializer<T extends SimpleIcon> implements IIconSerializer<T> {
        private final ResourceLocation name;
        private final Function<ResourceLocation, T> constructor;

        public Serializer(ResourceLocation name, Function<ResourceLocation, T> constructor) {
            this.name = name;
            this.constructor = constructor;
        }

        @Override
        public T deserialize(ResourceLocation id, JsonObject json) {
            T t = constructor.apply(id);
            t.group = JSONUtils.getString(json, "group", "");

            // Icon
            if (json.has("icon")) {
                JsonObject iconObj = json.getAsJsonObject("icon");
                String texturePath = JSONUtils.getString(iconObj, "texture");
                t.texture = parseTexturePath(texturePath);

                JsonObject textJson = iconObj.getAsJsonObject("text");
                t.text = textJson != null ? ITextComponent.Serializer.func_240641_a_(textJson) : StringTextComponent.EMPTY;
            }

            return t;
        }

        private static ResourceLocation parseTexturePath(String texturePath) {
            ResourceLocation raw = new ResourceLocation(texturePath);
            String fullPath = "textures/" + raw.getPath() + ".png";
            return new ResourceLocation(raw.getNamespace(), fullPath);
        }

        @Override
        public T read(ResourceLocation id, PacketBuffer buffer) {
            T t = constructor.apply(id);
            t.group = buffer.readString();

            // Icon
            ResourceLocation itemId = buffer.readResourceLocation();
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item != null) {
                t.texture = buffer.readResourceLocation();
                t.text = buffer.readTextComponent();
            }

            return t;
        }

        @Override
        public void write(PacketBuffer buffer, T icon) {
            buffer.writeString(icon.group);

            // Icon
            buffer.writeResourceLocation(icon.texture);
            buffer.writeTextComponent(icon.text);
        }

        @Override
        public ResourceLocation getName() {
            return name;
        }
    }
}
