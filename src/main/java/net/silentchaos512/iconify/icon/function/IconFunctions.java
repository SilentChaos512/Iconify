package net.silentchaos512.iconify.icon.function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.iconify.Iconify;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.api.icon.ITextFunctionSerializer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class IconFunctions {
    private static final Map<ResourceLocation, ITextFunctionSerializer<?>> REGISTRY = new HashMap<>();

    public static final ITextFunctionSerializer<?> ATTRIBUTE = register(new AttributeTextFunction.Serializer());
    public static final ITextFunctionSerializer<?> EMPTY = register(basicSerializer("empty", EmptyTextFunction::new));
    public static final ITextFunctionSerializer<?> FOOD = register(basicSerializer("food", FoodTextFunction::new));
    public static final ITextFunctionSerializer<?> GEAR_STAT = register(new GearStatFunction.Serializer());
    public static final ITextFunctionSerializer<?> SIMPLE = register(new SimpleTextFunction.Serializer());
    public static final ITextFunctionSerializer<?> DURABILITY = register(new ItemPropertyTextFunction.Serializer(Iconify.getId("durability"), IconFunctions::getMaxDamage));
    public static final ITextFunctionSerializer<?> HARVEST_LEVEL = register(new ItemPropertyTextFunction.Serializer(Iconify.getId("harvest_level"), IconFunctions::getHarvestLevel));
    public static final ITextFunctionSerializer<?> HARVEST_SPEED = register(new ItemPropertyTextFunction.Serializer(Iconify.getId("harvest_speed"), IconFunctions::getHarvestSpeed));

    public static <S extends ITextFunctionSerializer<T>, T extends ITextFunction> S register(S serializer) {
        ResourceLocation id = serializer.getName();
        if (REGISTRY.containsKey(id)) {
            throw new IllegalArgumentException("Already have text function with ID: " + id);
        }
        REGISTRY.put(id, serializer);
        return serializer;
    }

    private static <T extends ITextFunction> AbstractTextFunctionSerializer<T> basicSerializer(String name, Supplier<T> factory) {
        return AbstractTextFunctionSerializer.basic(Iconify.getId(name), factory);
    }

    public static ITextFunction deserialize(JsonElement json) {
        if (json.isJsonObject() && json.getAsJsonObject().has("function")) {
            String typeStr = JSONUtils.getString(json.getAsJsonObject(), "function");
            ITextFunctionSerializer<?> serializer = REGISTRY.get(Iconify.getIdWithDefaultNamespace(typeStr));
            if (serializer != null) {
                return serializer.deserialize(json.getAsJsonObject());
            }
            throw new JsonSyntaxException("Unknown text function: " + typeStr);
        }

        throw new JsonSyntaxException("Expected 'text' to be an object, was " + json);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ITextFunction> JsonObject serialize(T function) {
        ITextFunctionSerializer<T> serializer = (ITextFunctionSerializer<T>) function.getSerializer();
        JsonObject json = serializer.serialize(function);
        json.addProperty("function", function.getSerializer().getName().toString());
        return json;
    }

    public static ITextFunction read(PacketBuffer buffer) {
        ResourceLocation serializerId = buffer.readResourceLocation();
        ITextFunctionSerializer<?> serializer = REGISTRY.get(serializerId);
        if (serializer == null) {
            throw new IllegalStateException("Unknown text function serializer: " + serializerId);
        }
        return serializer.read(buffer);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ITextFunction> void write(PacketBuffer buffer, T function) {
        ITextFunctionSerializer<T> serializer = (ITextFunctionSerializer<T>) function.getSerializer();
        serializer.write(buffer, function);
    }

    @Nullable
    private static BlockState getBlockForTool(ItemStack stack) {
        if (stack.getToolTypes().contains(ToolType.PICKAXE)) {
            return Blocks.STONE.getDefaultState();
        }
        if (stack.getToolTypes().contains(ToolType.SHOVEL)) {
            return Blocks.DIRT.getDefaultState();
        }
        if (stack.getToolTypes().contains(ToolType.AXE)) {
            return Blocks.OAK_WOOD.getDefaultState();
        }
        if (stack.getToolTypes().contains(ToolType.HOE)) {
            return Blocks.PUMPKIN.getDefaultState();
        }
        return null;
    }

    private static Optional<ITextComponent> getMaxDamage(ItemStack stack) {
        if (stack.getMaxDamage() > 0) {
            return Optional.of(new StringTextComponent(String.valueOf(stack.getMaxDamage())));
        }
        return Optional.empty();
    }

    private static Optional<ITextComponent> getHarvestLevel(ItemStack stack) {
        int max = -1;

        for (ToolType toolType : stack.getToolTypes()) {
            max = Math.max(max, stack.getHarvestLevel(toolType, null, getBlockForTool(stack)));
        }

        if (max > 0) {
            return Optional.of(new StringTextComponent(String.valueOf(max)));
        }

        return Optional.empty();
    }

    private static Optional<ITextComponent> getHarvestSpeed(ItemStack stack) {
        BlockState blockForTool = getBlockForTool(stack);

        if (blockForTool != null) {
            float speed = stack.getDestroySpeed(blockForTool);
            if (speed > 1) {
                return Optional.of(new StringTextComponent(String.format("%.1f", speed)));
            }
        }

        return Optional.empty();
    }
}
