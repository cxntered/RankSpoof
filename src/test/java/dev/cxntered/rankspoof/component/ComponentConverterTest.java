package dev.cxntered.rankspoof.component;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComponentConverterTest {
    @Test
    void testLegacyFormattingConversion() {
        var legacyText = "§a§lauto§6fister";
        var expectedComponent = Component.empty()
                .append(Component.literal("auto").setStyle(Style.EMPTY.applyLegacyFormat(ChatFormatting.GREEN).withBold(true)))
                .append(Component.literal("fister").setStyle(Style.EMPTY.applyLegacyFormat(ChatFormatting.GOLD)));

        var actual = ComponentConverter.fromLegacyFormatting(legacyText);
        Assertions.assertEquals(expectedComponent, actual);
    }

    @Test
    void testLegacyRgbColorConversion() {
        var legacyText = "§#FF0000red §#00FF00green §#0000FFblue";
        var expectedComponent = Component.empty()
                .append(Component.literal("red ").withColor(0xFF0000))
                .append(Component.literal("green ").withColor(0x00FF00))
                .append(Component.literal("blue").withColor(0x0000FF));

        var actual = ComponentConverter.fromLegacyFormatting(legacyText);
        Assertions.assertEquals(expectedComponent, actual);
    }
}
