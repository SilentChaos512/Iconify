package net.silentchaos512.iconify.compat.gear;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

/**
 * Any code that directly accesses Silent Gear stuff should be here. Do not call directly! There are
 * an identical set of methods in SGearProxy which should be called instead. They will return
 * default values if Silent Gear is not installed.
 */
public final class SGearCompat {
    private SGearCompat() {
        throw new IllegalAccessError("Utility class");
    }

    public static Component formatStat(ResourceLocation statId, float value) {
        ItemStat stat = ItemStats.REGISTRY.get().getValue(statId);
        if (stat == null) return new TextComponent("UNKNOWN STAT");

        StatInstance instance = StatInstance.of(value);
        return instance.getFormattedText(stat, stat.isDisplayAsInt() ? 0 : 1, false);
    }

    public static float getStat(ItemStack stack, ResourceLocation statId) {
        ItemStat stat = ItemStats.REGISTRY.get().getValue(statId);

        if (stat != null && GearHelper.isGear(stack)) {
            return GearData.getStat(stack, stat);
        }

        return 0f;
    }

    public static boolean matchesGearType(ItemStack stack, String gearType) {
        GearType type = GearHelper.getType(stack);
        return type != GearType.NONE && type.matches(gearType);
    }
}
