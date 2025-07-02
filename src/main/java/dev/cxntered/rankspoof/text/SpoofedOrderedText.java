package dev.cxntered.rankspoof.text;

import dev.cxntered.rankspoof.RankSpoof;
import net.minecraft.text.*;

public class SpoofedOrderedText implements OrderedText {
    private final String string;

    public SpoofedOrderedText(OrderedText orderedText) {
        string = RankSpoof.getSpoofedText(LegacyFormatting.toLegacy(orderedText));
    }

    @Override
    public boolean accept(CharacterVisitor visitor) {
        return TextVisitFactory.visitFormatted(
                string,
                net.minecraft.text.Style.EMPTY,
                visitor
        );
    }
}
