package net.silentchaos512.iconify.icon.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.iconify.Iconify;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.api.icon.ITextFunctionSerializer;
import net.silentchaos512.utils.MathUtils;

import java.util.*;
import java.util.stream.Collectors;

public class AttributeTextFunction implements ITextFunction {
    private static final List<EquipmentSlot> ARMOR_SLOTS = ImmutableList.of(
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    );
    private static final List<EquipmentSlot> HAND_SLOTS = ImmutableList.of(
            EquipmentSlot.MAINHAND,
            EquipmentSlot.OFFHAND
    );

    private final List<EquipmentSlot> equipmentSlots = new ArrayList<>();
    private final Attribute attribute;

    public AttributeTextFunction(Collection<EquipmentSlot> equipmentSlots, Attribute attribute) {
        this.equipmentSlots.addAll(equipmentSlots);
        this.attribute = attribute;
    }

    @Override
    public Optional<Component> getText(ItemStack stack) {
        for (EquipmentSlot slot : this.equipmentSlots) {
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
                    return Optional.of(new TextComponent(str));
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
            String attributeName = GsonHelper.getAsString(json, "attribute");
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeName));
            if (attribute == null) {
                throw new JsonParseException("Unknown attribute '" + attributeName + "'");
            }

            JsonElement slotsJson = json.get("slots");
            List<EquipmentSlot> slots;
            if (slotsJson.isJsonArray()) {
                slots = new ArrayList<>();
                for (JsonElement arrayElement : slotsJson.getAsJsonArray()) {
                    slots.add(EquipmentSlot.byName(arrayElement.getAsString()));
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
        public AttributeTextFunction read(FriendlyByteBuf buffer) {
            ResourceLocation attributeId = buffer.readResourceLocation();
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
            List<EquipmentSlot> slots = new ArrayList<>();
            int count = buffer.readByte();
            for (int i = 0; i < count; ++i) {
                slots.add(EquipmentSlot.byName(buffer.readUtf()));
            }
            return new AttributeTextFunction(slots, attribute);
        }

        @Override
        public void write(FriendlyByteBuf buffer, AttributeTextFunction function) {
            buffer.writeResourceLocation(Objects.requireNonNull(function.attribute.getRegistryName()));
            buffer.writeByte(function.equipmentSlots.size());
            function.equipmentSlots.forEach(slot -> buffer.writeUtf(slot.getName()));
        }
    }
}
