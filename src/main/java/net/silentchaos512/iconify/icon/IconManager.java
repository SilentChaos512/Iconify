package net.silentchaos512.iconify.icon;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.silentchaos512.iconify.Iconify;
import net.silentchaos512.iconify.api.icon.IIcon;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class IconManager implements IResourceManagerReloadListener {
    public static final IconManager INSTANCE = new IconManager();

    public static final Marker MARKER = MarkerManager.getMarker("IconManager");

    private static final String DATA_PATH = "iconify_icons";
    private static final Map<ResourceLocation, IIcon> MAP = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Collection<String> ERROR_LIST = new ArrayList<>();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Collection<ResourceLocation> resources = resourceManager.listResources(DATA_PATH, s -> s.endsWith(".json"));
        if (resources.isEmpty()) return;

        synchronized (MAP) {
            MAP.clear();
            ERROR_LIST.clear();
            Iconify.LOGGER.info(MARKER, "Reloading icon files");

            for (ResourceLocation id : resources) {
                String path = id.getPath().substring(DATA_PATH.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

                String packName = "ERROR";
                try (IResource iresource = resourceManager.getResource(id)) {
                    packName = iresource.getSourceName();
                    JsonObject json = JSONUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                    if (json == null) {
                        Iconify.LOGGER.error(MARKER, "Could not load icon {} as it's null or empty", name);
                    } else if (!CraftingHelper.processConditions(json, "conditions")) {
                        Iconify.LOGGER.info(MARKER, "Skipping loading icon {} as its conditions were not met", name);
                    } else {
                        IIcon icon = IconSerializers.deserialize(name, json);
                        MAP.put(icon.getId(), icon);
                    }
                } catch (IllegalArgumentException | JsonParseException ex) {
                    Iconify.LOGGER.error(MARKER, "Parsing error loading icon {}", name, ex);
                    ERROR_LIST.add(String.format("%s (%s)", name, packName));
                } catch (IOException ex) {
                    Iconify.LOGGER.error(MARKER, "Could not read icon {}", name, ex);
                    ERROR_LIST.add(String.format("%s (%s)", name, packName));
                }
            }
        }
    }

    public static List<IIcon> getValues() {
        synchronized (MAP) {
            return ImmutableList.copyOf(MAP.values());
        }
    }

    @Nullable
    public static IIcon get(@Nullable ResourceLocation id) {
        if (id == null) return null;

        synchronized (MAP) {
            return MAP.get(id);
        }
    }
}
