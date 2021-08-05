package net.silentchaos512.iconify.icon.type;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.iconify.api.icon.IIconSerializer;
import net.silentchaos512.iconify.icon.IconSerializers;

import java.util.function.Function;

public class TagIcon extends SimpleIcon {
    private ITag.INamedTag<Item> tag = null;

    public TagIcon(ResourceLocation iconId) {
        super(iconId);
    }

    @Override
    public IIconSerializer<?> getSerializer() {
        return IconSerializers.TAG;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem().is(this.tag);
    }

    public static class Serializer extends SimpleIcon.Serializer<TagIcon> {
        public Serializer(ResourceLocation name, Function<ResourceLocation, TagIcon> constructor) {
            super(name, constructor);
        }

        @Override
        public TagIcon deserialize(ResourceLocation id, JsonObject json) {
            TagIcon icon = super.deserialize(id, json);
            String str = JSONUtils.getAsString(json, "tag");
            if (str.isEmpty()) {
                throw new JsonParseException("tag may not be empty");
            }
            icon.tag = ItemTags.bind(str);
            return icon;
        }

        @Override
        public TagIcon read(ResourceLocation id, PacketBuffer buffer) {
            TagIcon icon = super.read(id, buffer);
            icon.tag = ItemTags.bind(buffer.readUtf());
            return icon;
        }

        @Override
        public void write(PacketBuffer buffer, TagIcon icon) {
            super.write(buffer, icon);
            buffer.writeUtf(icon.tag.getName().toString());
        }
    }
}
