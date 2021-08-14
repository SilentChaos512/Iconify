package net.silentchaos512.iconify.icon.function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
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
            String typeStr = GsonHelper.getAsString(json.getAsJsonObject(), "function");
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

    public static ITextFunction read(FriendlyByteBuf buffer) {
        ResourceLocation serializerId = buffer.readResourceLocation();
        ITextFunctionSerializer<?> serializer = REGISTRY.get(serializerId);
        if (serializer == null) {
            throw new IllegalStateException("Unknown text function serializer: " + serializerId);
        }
        return serializer.read(buffer);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ITextFunction> void write(FriendlyByteBuf buffer, T function) {
        ITextFunctionSerializer<T> serializer = (ITextFunctionSerializer<T>) function.getSerializer();
        serializer.write(buffer, function);
    }

    @Nullable
    private static BlockState getBlockForTool(ItemStack stack, int level) {
        if (stack.canPerformAction(ToolActions.PICKAXE_DIG)) {
            return switch (level) {
                case 3 -> Blocks.OBSIDIAN.defaultBlockState();
                case 2 -> Blocks.DIAMOND_ORE.defaultBlockState();
                case 1 -> Blocks.IRON_ORE.defaultBlockState();
                case 0 -> Blocks.STONE.defaultBlockState();
                default -> null;
            };
        }
        if (stack.canPerformAction(ToolActions.SHOVEL_DIG)) {
            return Blocks.DIRT.defaultBlockState();
        }
        if (stack.canPerformAction(ToolActions.AXE_DIG)) {
            return Blocks.OAK_WOOD.defaultBlockState();
        }
        if (stack.canPerformAction(ToolActions.HOE_DIG)) {
            return Blocks.PUMPKIN.defaultBlockState();
        }
        return null;
    }

    private static Optional<Component> getMaxDamage(ItemStack stack) {
        if (stack.getMaxDamage() > 0) {
            return Optional.of(new TextComponent(String.valueOf(stack.getMaxDamage())));
        }
        return Optional.empty();
    }

    private static Optional<Component> getHarvestLevel(ItemStack stack) {
        int max = -1;

        // TODO: could probably clean this up somehow...
        for (int i = 4; i >= 0; --i) {
            if (getBlockForTool(stack, i) != null) {
                max = i;
                break;
            }
        }

        if (max > 0) {
            return Optional.of(new TextComponent(String.valueOf(max)));
        }

        return Optional.empty();
    }

    private static Optional<Component> getHarvestSpeed(ItemStack stack) {
        BlockState blockForTool = getBlockForTool(stack, 0);

        if (blockForTool != null) {
            float speed = stack.getDestroySpeed(blockForTool);
            if (speed > 1) {
                return Optional.of(new TextComponent(String.format("%.1f", speed)));
            }
        }

        return Optional.empty();
    }
}
