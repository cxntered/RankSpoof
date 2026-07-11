package dev.cxntered.rankspoof.config;

//~ if <26.1 'GuiGraphicsExtractor' -> 'GuiGraphics' {
import dev.cxntered.rankspoof.component.ComponentConverter;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class RankPreview implements ImageRenderer {
    public static String rank;
    public static boolean isRendering = false;

    @Override
    public int render(GuiGraphicsExtractor graphics, int x, int y, int width, float v) {
        isRendering = true;

        Font font = Minecraft.getInstance().font;
        Component rankPreview = ComponentConverter.fromLegacyFormatting(rank.replace('&', '§') + " " + Minecraft.getInstance().getUser().getName());

        List<FormattedCharSequence> lines = font.split(rankPreview, width - 10);
        int textHeight = lines.size() * font.lineHeight;
        int totalHeight = textHeight + 10;

        //~ if <26.1 '.extractTooltipBackground' -> '.renderTooltipBackground'
        TooltipRenderUtil.extractTooltipBackground(
                graphics,
                x + 5,
                y + 5,
                width - 10,
                totalHeight - 10,
                null
        );

        int textY = y + 6;
        for (FormattedCharSequence line : lines) {
            int lineWidth = font.width(line);
            int centeredX = x + 5 + ((width - 10) - lineWidth) / 2;
            //~ if <26.1 '.text' -> '.drawString'
            graphics.text(font, line, centeredX, textY, -1);
            textY += font.lineHeight;
        }

        isRendering = false;
        return totalHeight;
    }

    @Override
    public void close() {
    }
}
//~}
