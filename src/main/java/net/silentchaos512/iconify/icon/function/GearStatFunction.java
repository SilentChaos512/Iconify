package net.silentchaos512.iconify.icon.function;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.silentchaos512.iconify.Iconify;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.api.icon.ITextFunctionSerializer;
import net.silentchaos512.iconify.compat.gear.SGearProxy;

import java.util.Optional;

public class GearStatFunction implements ITextFunction {
    private final ResourceLocation statId;

    public GearStatFunction(ResourceLocation statId) {
        this.statId = statId;
    }

    @Override
    public Optional<Component> getText(ItemStack stack) {
        float stat = SGearProxy.getStat(stack, this.statId);
        if (stat > 0f) {
            return Optional.of(SGearProxy.formatStat(this.statId, stat));
        }
        return Optional.empty();
    }

    @Override
    public ITextFunctionSerializer<?> getSerializer() {
        return IconFunctions.GEAR_STAT;
    }

    public static final class Serializer extends AbstractTextFunctionSerializer<GearStatFunction> {
        public Serializer() {
            super(Iconify.getId("gear_stat"));
        }

        @Override
        public GearStatFunction deserialize(JsonObject json) {
            String statName = GsonHelper.getAsString(json, "stat");
            ResourceLocation statId = withSgearNamespace(statName);
            return new GearStatFunction(statId);
        }

        @Override
        public JsonObject serialize(GearStatFunction function) {
            JsonObject json = new JsonObject();
            json.addProperty("stat", function.statId.toString());
            return json;
        }

        @Override
        public GearStatFunction read(FriendlyByteBuf buffer) {
            return new GearStatFunction(buffer.readResourceLocation());
        }

        @Override
        public void write(FriendlyByteBuf buffer, GearStatFunction function) {
            buffer.writeResourceLocation(function.statId);
        }

        private static ResourceLocation withSgearNamespace(String str) {
            return str.contains(":") ? new ResourceLocation(str) : new ResourceLocation("silentgear", str);
        }
    }
}
