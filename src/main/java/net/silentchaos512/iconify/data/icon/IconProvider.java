package net.silentchaos512.iconify.data.icon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.iconify.api.icon.IIconSerializer;
import net.silentchaos512.iconify.icon.IconSerializers;
import net.silentchaos512.iconify.icon.function.FoodTextFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

public class IconProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    private final DataGenerator generator;
    private final String modId;
    private final Collection<IconBuilder> builders = new ArrayList<>();

    public IconProvider(DataGenerator generator, String modId) {
        this.generator = generator;
        this.modId = modId;
    }

    private void getIcons() {
        simpleIcon("food")
                .group("test")
                .texture(new ResourceLocation("item/cooked_chicken"))
                .text(new FoodTextFunction());
        modIdIcon("silentgear_test", "silentgear")
                .group("mod_icons")
                .texture(new ResourceLocation("silentgear", "item/blueprint_paper"));
    }

    protected ResourceLocation modId(String id) {
        return new ResourceLocation(this.modId, id);
    }

    protected  <T extends IconBuilder> T builder(String id, Function<String, T> function) {
        T builder = function.apply(id);
        builders.add(builder);
        return builder;
    }

    public IconBuilder builder(String id, IIconSerializer<?> serializer) {
        return builder(id, s -> new IconBuilder(modId(s), serializer));
    }

    public IconBuilder simpleIcon(String id) {
        return builder(id, IconSerializers.SIMPLE);
    }

    public ModIdIconBuilder modIdIcon(String id, String modIdIn) {
        return builder(id, s -> new ModIdIconBuilder(modId(id), modIdIn));
    }

    @Override
    public String getName() {
        return "Iconify Icons: " + modId;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        Path outputFolder = this.generator.getOutputFolder();

        builders.clear();
        getIcons();

        for (IconBuilder builder : builders) {
            try {
                String jsonStr = GSON.toJson(builder.serialize());
                String hashStr = HASH_FUNCTION.hashUnencodedChars(jsonStr).toString();
                Path path = outputFolder.resolve(String.format("data/%s/iconify_icons/%s.json", builder.id.getNamespace(), builder.id.getPath()));
                if (!Objects.equals(cache.getPreviousHash(outputFolder), hashStr) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                        writer.write(jsonStr);
                    }
                }

                cache.recordHash(path, hashStr);
            } catch (IOException ex) {
                LOGGER.error("Could not save materials to {}", outputFolder, ex);
            }
        }
    }
}
