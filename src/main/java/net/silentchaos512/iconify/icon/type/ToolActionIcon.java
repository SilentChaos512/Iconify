package net.silentchaos512.iconify.icon.type;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ToolAction;
import net.silentchaos512.iconify.api.icon.IIconSerializer;
import net.silentchaos512.iconify.icon.IconSerializers;

import java.util.function.Function;

public class ToolActionIcon extends SimpleIcon {
    private ToolAction toolAction = null;

    public ToolActionIcon(ResourceLocation iconId) {
        super(iconId);
    }

    @Override
    public IIconSerializer<?> getSerializer() {
        return IconSerializers.TOOL_ACTION;
    }

    @Override
    public boolean test(ItemStack stack) {
        return toolAction != null && stack.canPerformAction(toolAction);
    }

    public static class Serializer extends SimpleIcon.Serializer<ToolActionIcon> {
        public Serializer(ResourceLocation name, Function<ResourceLocation, ToolActionIcon> constructor) {
            super(name, constructor);
        }

        @Override
        public ToolActionIcon deserialize(ResourceLocation id, JsonObject json) {
            ToolActionIcon icon = super.deserialize(id, json);
            String str = GsonHelper.getAsString(json, "tool_action");
            if (str.isEmpty()) {
                throw new JsonParseException("tool_action may not be empty");
            }
            icon.toolAction = ToolAction.get(str);
            return icon;
        }

        @Override
        public ToolActionIcon read(ResourceLocation id, FriendlyByteBuf buffer) {
            ToolActionIcon icon = super.read(id, buffer);
            icon.toolAction = ToolAction.get(buffer.readUtf());
            return icon;
        }

        @Override
        public void write(FriendlyByteBuf buffer, ToolActionIcon icon) {
            super.write(buffer, icon);
            buffer.writeUtf(icon.toolAction.name());
        }
    }
}
