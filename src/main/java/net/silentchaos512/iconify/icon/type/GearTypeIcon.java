package net.silentchaos512.iconify.icon.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.iconify.api.icon.IIconSerializer;
import net.silentchaos512.iconify.compat.gear.SGearProxy;
import net.silentchaos512.iconify.icon.IconSerializers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GearTypeIcon extends SimpleIcon {
    private final List<String> gearTypes = new ArrayList<>();

    public GearTypeIcon(ResourceLocation iconId) {
        super(iconId);
    }

    @Override
    public IIconSerializer<?> getSerializer() {
        return IconSerializers.GEAR_TYPE;
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.gearTypes.stream().anyMatch(gearType -> SGearProxy.matchesGearType(stack, gearType));
    }

    public static class Serializer extends SimpleIcon.Serializer<GearTypeIcon> {
        public Serializer(ResourceLocation name, Function<ResourceLocation, GearTypeIcon> constructor) {
            super(name, constructor);
        }

        @Override
        public GearTypeIcon deserialize(ResourceLocation id, JsonObject json) {
            GearTypeIcon icon = super.deserialize(id, json);

            JsonElement gearTypeJson = json.get("gear_type");
            if (gearTypeJson.isJsonPrimitive()) {
                icon.gearTypes.add(gearTypeJson.getAsString());
            } else if (gearTypeJson.isJsonArray()) {
                JsonArray array = gearTypeJson.getAsJsonArray();
                for (JsonElement je : array) {
                    icon.gearTypes.add(je.getAsString());
                }
            } else {
                throw new JsonParseException("Expected gear_type to be string or array");
            }

            return icon;
        }

        @Override
        public GearTypeIcon read(ResourceLocation id, PacketBuffer buffer) {
            GearTypeIcon icon = super.read(id, buffer);
            int typeCount = buffer.readByte();
            for (int i = 0; i < typeCount; ++i) {
                icon.gearTypes.add(buffer.readUtf());
            }
            return icon;
        }

        @Override
        public void write(PacketBuffer buffer, GearTypeIcon icon) {
            super.write(buffer, icon);
            buffer.writeByte(icon.gearTypes.size());
            icon.gearTypes.forEach(buffer::writeUtf);
        }
    }
}
