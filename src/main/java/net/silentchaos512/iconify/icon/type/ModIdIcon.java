package net.silentchaos512.iconify.icon.type;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.iconify.api.icon.IIconSerializer;
import net.silentchaos512.iconify.icon.IconSerializers;
import net.silentchaos512.lib.util.NameUtils;

import java.util.function.Function;

public class ModIdIcon extends SimpleIcon {
    private String modId = null;

    public ModIdIcon(ResourceLocation iconId) {
        super(iconId);
    }

    @Override
    public IIconSerializer<?> getSerializer() {
        return IconSerializers.MOD_ID;
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.modId.equals(NameUtils.fromItem(stack).getNamespace());
    }

    public static class Serializer extends SimpleIcon.Serializer<ModIdIcon> {
        public Serializer(ResourceLocation name, Function<ResourceLocation, ModIdIcon> constructor) {
            super(name, constructor);
        }

        @Override
        public ModIdIcon deserialize(ResourceLocation id, JsonObject json) {
            ModIdIcon icon = super.deserialize(id, json);
            icon.modId = GsonHelper.getAsString(json, "mod_id");
            return icon;
        }

        @Override
        public ModIdIcon read(ResourceLocation id, FriendlyByteBuf buffer) {
            ModIdIcon icon = super.read(id, buffer);
            icon.modId = buffer.readUtf();
            return icon;
        }

        @Override
        public void write(FriendlyByteBuf buffer, ModIdIcon icon) {
            super.write(buffer, icon);
            buffer.writeUtf(icon.modId);
        }
    }
}
