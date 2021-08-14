package net.silentchaos512.iconify.data.icon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.iconify.api.icon.IIconSerializer;
import net.silentchaos512.iconify.api.icon.ITextFunction;
import net.silentchaos512.iconify.icon.function.IconFunctions;

public class IconBuilder {
    final ResourceLocation id;
    private final IIconSerializer<?> serializer;

    private String group = "undefined";
    private ResourceLocation texture = new ResourceLocation("item/barrier");
    private ITextFunction text = null;

    public IconBuilder(ResourceLocation id, IIconSerializer<?> serializer) {
        this.id = id;
        this.serializer = serializer;
    }

    public IconBuilder group(String group) {
        this.group = group;
        return this;
    }

    public IconBuilder texture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    public IconBuilder text(ITextFunction text) {
        this.text = text;
        return this;
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();

        json.addProperty("type", serializer.getName().toString());
        json.addProperty("group", group);

        JsonObject iconJson = new JsonObject();
        iconJson.addProperty("texture", texture.toString());
        if (text != null) {
            iconJson.add("text", IconFunctions.serialize(text));
        }
        json.add("icon", iconJson);

        return json;
    }
}
