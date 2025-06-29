package dev.cxntered.rankspoof.config;

import dev.isxander.yacl3.gui.image.ImageRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class RankPreview implements ImageRenderer {
    public static String rank;
    public static boolean isRendering = false;

    @Override
    public int render(DrawContext drawContext, int x, int y, int width, float v) {
        isRendering = true;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        String text = (rank.isEmpty() ? "" : rank.replace('&', '§') + " ") + MinecraftClient.getInstance().getSession().getUsername();

        List<OrderedText> lines = textRenderer.wrapLines(Text.of(text), width - 10);
        int textHeight = lines.size() * textRenderer.fontHeight;
        int totalHeight = textHeight + 10;

        TooltipBackgroundRenderer.render(drawContext, x + 5, y + 5, width - 10, totalHeight - 10, null);

        int textY = y + 6;
        for (OrderedText line : lines) {
            int lineWidth = textRenderer.getWidth(line);
            int centeredX = x + 5 + ((width - 10) - lineWidth) / 2;
            drawContext.drawTextWithShadow(textRenderer, line, centeredX, textY, -1);
            textY += textRenderer.fontHeight;
        }

        isRendering = false;

        return totalHeight;
    }

    @Override
    public void close() {
    }
}
