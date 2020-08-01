package net.silentchaos512.iconify.icon.type;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.iconify.api.icon.IIconSerializer;
import net.silentchaos512.iconify.icon.IconSerializers;

import java.util.function.Function;

public class ToolTypeIcon extends SimpleIcon {
    private ToolType toolType = null;

    public ToolTypeIcon(ResourceLocation iconId) {
        super(iconId);
    }

    @Override
    public IIconSerializer<?> getSerializer() {
        return IconSerializers.TOOL_TYPE;
    }

    @Override
    public boolean test(ItemStack stack) {
        return toolType != null && stack.getToolTypes().contains(this.toolType);
    }

    public static class Serializer extends SimpleIcon.Serializer<ToolTypeIcon> {
        public Serializer(ResourceLocation name, Function<ResourceLocation, ToolTypeIcon> constructor) {
            super(name, constructor);
        }

        @Override
        public ToolTypeIcon deserialize(ResourceLocation id, JsonObject json) {
            ToolTypeIcon icon = super.deserialize(id, json);
            String str = JSONUtils.getString(json, "tool_type");
            if (str.isEmpty()) {
                throw new JsonParseException("tool_type may not be empty");
            }
            icon.toolType = ToolType.get(str);
            return icon;
        }

        @Override
        public ToolTypeIcon read(ResourceLocation id, PacketBuffer buffer) {
            ToolTypeIcon icon = super.read(id, buffer);
            icon.toolType = ToolType.get(buffer.readString());
            return icon;
        }

        @Override
        public void write(PacketBuffer buffer, ToolTypeIcon icon) {
            super.write(buffer, icon);
            buffer.writeString(icon.toolType.getName());
        }
    }
}
