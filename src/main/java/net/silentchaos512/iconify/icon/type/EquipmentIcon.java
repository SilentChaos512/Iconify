package net.silentchaos512.iconify.icon.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.iconify.api.icon.IIconSerializer;
import net.silentchaos512.iconify.icon.IconSerializers;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

public class EquipmentIcon extends SimpleIcon {
    private final Set<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);

    public EquipmentIcon(ResourceLocation iconId) {
        super(iconId);
    }

    @Override
    public IIconSerializer<?> getSerializer() {
        return IconSerializers.EQUIPMENT_TYPE;
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.slots.contains(getEquipmentSlot(stack));
    }

    private static EquipmentSlot getEquipmentSlot(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem) {
            return ((ArmorItem) stack.getItem()).getSlot();
        }
        return stack.getEquipmentSlot();
    }

    public static class Serializer extends SimpleIcon.Serializer<EquipmentIcon> {
        public Serializer(ResourceLocation name, Function<ResourceLocation, EquipmentIcon> constructor) {
            super(name, constructor);
        }

        @Override
        public EquipmentIcon deserialize(ResourceLocation id, JsonObject json) {
            EquipmentIcon icon =  super.deserialize(id, json);

            JsonElement element = json.get("slots");
            if (element.isJsonPrimitive()) {
                icon.slots.add(EquipmentSlot.valueOf(element.getAsString().toUpperCase(Locale.ROOT)));
            } else if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement je : array) {
                    icon.slots.add(EquipmentSlot.valueOf(je.getAsString().toUpperCase(Locale.ROOT)));
                }
            } else {
                throw new JsonParseException("Expected 'slots' to be string or array");
            }

            return icon;
        }

        @Override
        public EquipmentIcon read(ResourceLocation id, FriendlyByteBuf buffer) {
            EquipmentIcon icon = super.read(id, buffer);
            int slotCount = buffer.readByte();
            for (int i = 0; i < slotCount; ++i) {
                icon.slots.add(buffer.readEnum(EquipmentSlot.class));
            }
            return icon;
        }

        @Override
        public void write(FriendlyByteBuf buffer, EquipmentIcon icon) {
            super.write(buffer, icon);
            buffer.writeByte(icon.slots.size());
            icon.slots.forEach(buffer::writeEnum);
        }
    }
}
