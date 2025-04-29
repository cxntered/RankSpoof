package dev.cxntered.rankspoof.text;

import dev.cxntered.rankspoof.RankSpoof;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TextVisitFactory;

public class SpoofedOrderedText implements OrderedText {
    private final String string;

    public SpoofedOrderedText(OrderedText orderedText) {
        string = RankSpoof.getSpoofedText(getFormattedString(orderedText));
    }

    @Override
    public boolean accept(CharacterVisitor visitor) {
        return TextVisitFactory.visitFormatted(
                string,
                net.minecraft.text.Style.EMPTY,
                visitor
        );
    }

    public static String getFormattedString(OrderedText orderedText) {
        final TextComponent.Builder text = Component.text();

        orderedText.accept((index, style, codePoint) -> {
            text.append(Component.text()
                    .content(String.valueOf(Character.toChars(codePoint)))
                    .style(mcToAdventureStyle(style)));
            return true;
        });

        return LegacyComponentSerializer.legacySection().serialize(text.build());
    }

    private static Style mcToAdventureStyle(net.minecraft.text.Style mcStyle) {
        return Style.style()
                .decoration(TextDecoration.BOLD, mcStyle.isBold())
                .decoration(TextDecoration.ITALIC, mcStyle.isItalic())
                .decoration(TextDecoration.UNDERLINED, mcStyle.isUnderlined())
                .decoration(TextDecoration.STRIKETHROUGH, mcStyle.isStrikethrough())
                .decoration(TextDecoration.OBFUSCATED, mcStyle.isObfuscated())
                .color(mcStyle.getColor() != null ? TextColor.color(mcStyle.getColor().getRgb()) : null)
                .build();
    }
}
