package net.silentchaos512.iconify.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.silentchaos512.iconify.Iconify;
import net.silentchaos512.iconify.data.icon.IconProvider;

@Mod.EventBusSubscriber(modid = Iconify.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(new IconProvider(gen, Iconify.MOD_ID));
    }
}
