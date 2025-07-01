package dev.cxntered.rankspoof.text;

import dev.cxntered.rankspoof.RankSpoof;
import net.minecraft.text.*;

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
        MutableText text = Text.empty();

        orderedText.accept((index, style, codePoint) -> {
            text.append(Text.literal(Character.toString(codePoint)).setStyle(style));
            return true;
        });

        return LegacyFormatting.toLegacy(text);
    }
}
