package net.silentchaos512.iconify.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.iconify.Iconify;
import net.silentchaos512.iconify.api.icon.IIcon;
import net.silentchaos512.iconify.icon.IconManager;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = Iconify.MOD_ID, value = Dist.CLIENT)
public final class TooltipHandler {
    private TooltipHandler() {}

    @SubscribeEvent
    public static void onItemTooltip(RenderTooltipEvent.PostBackground event) {
        if (event.getStack().isEmpty()) {
            return;
        }

        final int startX = event.getX() - 2;
        int x = startX;
        int y = event.getY() - 14;

        for (IIcon icon : IconManager.getValues()) {
            if (isIconVisible(icon, event.getStack())) {
                renderTexture(icon.getIconTexture(), 0.7f, x, y, 16, 16);
                x += 11;

                // render text
                Optional<Component> text = icon.getIconText(event.getStack());
                if (text.isPresent()) {
                    x += renderText(event, text.get(), x + 2, y);
                }

                if (x > event.getX() + event.getWidth()) {
                    x = startX;
                    y -= 11;
                }
            }
        }
    }

    private static boolean isIconVisible(IIcon icon, ItemStack stack) {
        return icon.test(stack) && (icon.isVisibleWhenTextEmpty() || icon.getIconText(stack).isPresent());
    }

    private static int renderText(RenderTooltipEvent event, Component text, int x, int y) {
        event.getMatrixStack().pushPose();
        float scale = 0.7f;
        event.getMatrixStack().scale(scale, scale, scale);
        event.getFontRenderer().drawShadow(event.getMatrixStack(), text.getVisualOrderText(), x / scale, y / scale + 4, -1);
        event.getMatrixStack().popPose();

        int length = event.getFontRenderer().width(text);
        int spacing = length > 0 ? 4 : 0;
        return Math.round(length * scale) + spacing;
    }

    public static void renderTexture(ResourceLocation texture, float scale, int x, int y, int width, int height) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, texture);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(x, y + height * scale, 0.0)
                .uv(0, 1)
                .color(255, 255, 255, 255).endVertex();
        bufferbuilder.vertex(x + width * scale, y + height * scale, 0.0)
                .uv(1, 1)
                .color(255, 255, 255, 255).endVertex();
        bufferbuilder.vertex(x + width * scale, y, 0.0)
                .uv(1, 0)
                .color(255, 255, 255, 255).endVertex();
        bufferbuilder.vertex(x, y, 0.0)
                .uv(0, 0)
                .color(255, 255, 255, 255).endVertex();
        tessellator.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    private void drawItemStack(ItemStack stack, int x, int y) {
//        RenderSystem.translatef(0.0F, 0.0F, 32.0F);
//        this.setBlitOffset(200);
//        this.itemRenderer.zLevel = 200.0F;
//        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
//        if (font == null) font = this.font;
//        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
//        this.setBlitOffset(0);
//        this.itemRenderer.zLevel = 0.0F;
    }
}
