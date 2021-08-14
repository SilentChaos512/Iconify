package net.silentchaos512.iconify.compat.gear;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fml.ModList;

public final class SGearProxy {
    private static boolean modLoaded = false;

    private SGearProxy() {
        throw new IllegalAccessError("Utility class");
    }

    public static void detectSilentGear() {
        modLoaded = ModList.get().isLoaded("silentgear");
    }

    public static boolean isLoaded() {
        return modLoaded;
    }

    public static Component formatStat(ResourceLocation statId, float value) {
        if (modLoaded) return SGearCompat.formatStat(statId, value);
        return new TextComponent("");
    }

    public static float getStat(ItemStack stack, ResourceLocation statId) {
        if (modLoaded) return SGearCompat.getStat(stack, statId);
        return 0f;
    }

    public static boolean matchesGearType(ItemStack stack, String gearType) {
        if (modLoaded) return SGearCompat.matchesGearType(stack, gearType);
        return false;
    }
}
