package net.silentchaos512.iconify.data.icon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.iconify.icon.IconSerializers;

public class ModIdIconBuilder extends IconBuilder {
    private final String modId;

    public ModIdIconBuilder(ResourceLocation id, String modId) {
        super(id, IconSerializers.MOD_ID);
        this.modId = modId;
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = super.serialize();
        json.addProperty("mod_id", this.modId);
        return json;
    }
}
