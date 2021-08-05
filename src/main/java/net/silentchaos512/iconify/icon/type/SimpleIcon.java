package net.silentchaos512.iconify.icon.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.iconify.api.icon.IIcon;
import net.silentchaos512.iconify.api.icon.IIconSerializer;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.icon.IconSerializers;
import net.silentchaos512.iconify.icon.function.EmptyTextFunction;
import net.silentchaos512.iconify.icon.function.IconFunctions;

import java.util.Optional;
import java.util.function.Function;

public class SimpleIcon implements IIcon {
    private final ResourceLocation iconId;
    String group = "";
    ResourceLocation texture;
    ITextFunction text = EmptyTextFunction.INSTANCE;
    boolean visibleWhenTextEmpty = true;

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
    public Optional<ITextComponent> getIconText(ItemStack stack) {
        return text.getText(stack);
    }

    @Override
    public IIconSerializer<?> getSerializer() {
        return IconSerializers.SIMPLE;
    }

    @Override
    public boolean test(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isVisibleWhenTextEmpty() {
        return visibleWhenTextEmpty;
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
            t.group = JSONUtils.getAsString(json, "group", "");
            t.visibleWhenTextEmpty = JSONUtils.getAsBoolean(json, "visible_when_text_empty", true);

            // Icon
            if (json.has("icon")) {
                JsonObject iconObj = json.getAsJsonObject("icon");
                String texturePath = JSONUtils.getAsString(iconObj, "texture");
                t.texture = parseTexturePath(texturePath);

                JsonElement textJson = iconObj.get("text");
                if (textJson != null) {
                    t.text = IconFunctions.deserialize(textJson);
                }
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
            t.group = buffer.readUtf();
            t.visibleWhenTextEmpty = buffer.readBoolean();

            // Icon
            ResourceLocation itemId = buffer.readResourceLocation();
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item != null) {
                t.texture = buffer.readResourceLocation();
                if (buffer.readBoolean()) {
                    t.text = IconFunctions.read(buffer);
                }
            }

            return t;
        }

        @Override
        public void write(PacketBuffer buffer, T icon) {
            buffer.writeUtf(icon.group);
            buffer.writeBoolean(icon.visibleWhenTextEmpty);

            // Icon
            buffer.writeResourceLocation(icon.texture);
            boolean hasText = !(icon.text instanceof EmptyTextFunction);
            buffer.writeBoolean(hasText);
            if (hasText) {
                IconFunctions.write(buffer, icon.text);
            }
        }

        @Override
        public ResourceLocation getName() {
            return name;
        }
    }
}
