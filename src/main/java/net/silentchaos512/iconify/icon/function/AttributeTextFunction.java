package net.silentchaos512.iconify.icon.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.iconify.Iconify;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.api.icon.ITextFunctionSerializer;
import net.silentchaos512.utils.MathUtils;

import java.util.*;
import java.util.stream.Collectors;

public class AttributeTextFunction implements ITextFunction {
    private static final List<EquipmentSlotType> ARMOR_SLOTS = ImmutableList.of(
            EquipmentSlotType.HEAD,
            EquipmentSlotType.CHEST,
            EquipmentSlotType.LEGS,
            EquipmentSlotType.FEET
    );
    private static final List<EquipmentSlotType> HAND_SLOTS = ImmutableList.of(
            EquipmentSlotType.MAINHAND,
            EquipmentSlotType.OFFHAND
    );

    private final List<EquipmentSlotType> equipmentSlots = new ArrayList<>();
    private final Attribute attribute;

    public AttributeTextFunction(Collection<EquipmentSlotType> equipmentSlots, Attribute attribute) {
        this.equipmentSlots.addAll(equipmentSlots);
        this.attribute = attribute;
    }

    @Override
    public Optional<ITextComponent> getText(ItemStack stack) {
        for (EquipmentSlotType slot : this.equipmentSlots) {
            Multimap<Attribute, AttributeModifier> map = stack.getAttributeModifiers(slot);
            Collection<AttributeModifier> mods = map.get(this.attribute);

            if (!mods.isEmpty()) {
                String str = mods.stream()
                        .filter(m -> !MathUtils.doublesEqual(m.getAmount(), 0.0))
                        .map(m -> String.format("%s%.1f",
                                m.getOperation() == AttributeModifier.Operation.ADDITION ? "" : "x",
                                m.getAmount()))
                        .collect(Collectors.joining(", "));

                if (!str.isEmpty()) {
                    return Optional.of(new StringTextComponent(str));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public ITextFunctionSerializer<?> getSerializer() {
        return IconFunctions.ATTRIBUTE;
    }

    public static final class Serializer extends AbstractTextFunctionSerializer<AttributeTextFunction> {
        public Serializer() {
            super(Iconify.getId("attribute"));
        }

        @Override
        public AttributeTextFunction deserialize(JsonObject json) {
            String attributeName = JSONUtils.getAsString(json, "attribute");
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeName));
            if (attribute == null) {
                throw new JsonParseException("Unknown attribute '" + attributeName + "'");
            }

            JsonElement slotsJson = json.get("slots");
            List<EquipmentSlotType> slots;
            if (slotsJson.isJsonArray()) {
                slots = new ArrayList<>();
                for (JsonElement arrayElement : slotsJson.getAsJsonArray()) {
                    slots.add(EquipmentSlotType.byName(arrayElement.getAsString()));
                }
            } else {
                String value = slotsJson.getAsString();
                if ("armor".equals(value)) {
                    slots = ARMOR_SLOTS;
                } else if ("hand".equals(value)) {
                    slots = HAND_SLOTS;
                } else {
                    throw new JsonParseException("Unknown slot group: " + value);
                }
            }

            return new AttributeTextFunction(slots, attribute);
        }

        @Override
        public JsonObject serialize(AttributeTextFunction function) {
            JsonObject json = new JsonObject();
            ResourceLocation attributeId = Objects.requireNonNull(function.attribute.getRegistryName());
            json.addProperty("attribute", attributeId.toString());
            JsonArray array = new JsonArray();
            function.equipmentSlots.forEach(slot -> array.add(slot.getName()));
            json.add("slots", array);
            return json;
        }

        @Override
        public AttributeTextFunction read(PacketBuffer buffer) {
            ResourceLocation attributeId = buffer.readResourceLocation();
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
            List<EquipmentSlotType> slots = new ArrayList<>();
            int count = buffer.readByte();
            for (int i = 0; i < count; ++i) {
                slots.add(EquipmentSlotType.byName(buffer.readUtf()));
            }
            return new AttributeTextFunction(slots, attribute);
        }

        @Override
        public void write(PacketBuffer buffer, AttributeTextFunction function) {
            buffer.writeResourceLocation(Objects.requireNonNull(function.attribute.getRegistryName()));
            buffer.writeByte(function.equipmentSlots.size());
            function.equipmentSlots.forEach(slot -> buffer.writeUtf(slot.getName()));
        }
    }
}
