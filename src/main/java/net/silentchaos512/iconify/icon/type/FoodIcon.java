package net.silentchaos512.iconify.icon.type;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.iconify.api.icon.IIconSerializer;
import net.silentchaos512.iconify.icon.IconSerializers;

import java.util.function.Function;

public class FoodIcon extends SimpleIcon {
    public FoodIcon(ResourceLocation iconId) {
        super(iconId);
    }

    @Override
    public IIconSerializer<?> getSerializer() {
        return IconSerializers.FOOD;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.isEdible();
    }

    public static class Serializer extends SimpleIcon.Serializer<FoodIcon> {
        public Serializer(ResourceLocation name, Function<ResourceLocation, FoodIcon> constructor) {
            super(name, constructor);
        }

        @Override
        public FoodIcon deserialize(ResourceLocation id, JsonObject json) {
            return super.deserialize(id, json);
        }

        @Override
        public FoodIcon read(ResourceLocation id, FriendlyByteBuf buffer) {
            return super.read(id, buffer);
        }

        @Override
        public void write(FriendlyByteBuf buffer, FoodIcon icon) {
            super.write(buffer, icon);
        }
    }
}
