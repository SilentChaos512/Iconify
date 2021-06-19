package net.silentchaos512.iconify.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.iconify.Iconify;
import net.silentchaos512.iconify.api.icon.IIcon;
import net.silentchaos512.iconify.icon.IconManager;
import org.lwjgl.opengl.GL11;

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
                Optional<ITextComponent> text = icon.getIconText(event.getStack());
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

    private static int renderText(RenderTooltipEvent event, ITextComponent text, int x, int y) {
        event.getMatrixStack().push();
        float scale = 0.7f;
        event.getMatrixStack().scale(scale, scale, scale);
        event.getFontRenderer().func_238407_a_(event.getMatrixStack(), text.func_241878_f(), x / scale, y / scale + 4, -1);
        event.getMatrixStack().pop();
        int length = event.getFontRenderer().getStringPropertyWidth(text);
        int spacing = length > 0 ? 4 : 0;
        return Math.round(length * scale) + spacing;
    }

    public static void renderTexture(ResourceLocation texture, float scale, int x, int y, int width, int height) {
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderSystem.defaultAlphaFunc();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x, y + height * scale, 0.0)
                .tex(0, 1)
                .color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(x + width * scale, y + height * scale, 0.0)
                .tex(1, 1)
                .color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(x + width * scale, y, 0.0)
                .tex(1, 0)
                .color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(x, y, 0.0)
                .tex(0, 0)
                .color(255, 255, 255, 255).endVertex();
        tessellator.draw();

        RenderSystem.disableBlend();
        RenderSystem.color4f(1f, 1f, 1f, 1f);

        RenderSystem.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
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
