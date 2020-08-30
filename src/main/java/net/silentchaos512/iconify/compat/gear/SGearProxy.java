package net.silentchaos512.iconify.compat.gear;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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

    public static ITextComponent formatStat(ResourceLocation statId, float value) {
        if (modLoaded) return SGearCompat.formatStat(statId, value);
        return new StringTextComponent("");
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
