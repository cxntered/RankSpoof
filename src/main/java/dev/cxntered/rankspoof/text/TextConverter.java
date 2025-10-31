package dev.cxntered.rankspoof.text;

import net.minecraft.text.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class TextConverter {
    public static Text fromOrderedText(OrderedText orderedText) {
        MutableText text = Text.empty();
        StringBuilder currentSection = new StringBuilder();
        AtomicReference<Style> currentStyle = new AtomicReference<>();

        orderedText.accept((index, style, codePoint) -> {
            if (currentStyle.get() == null) {
                currentStyle.set(style);
            } else if (currentStyle.get() != style) {
                if (!currentSection.isEmpty()) {
                    text.append(Text.literal(currentSection.toString()).setStyle(currentStyle.get()));
                    currentSection.setLength(0);
                }
                currentStyle.set(style);
            }
            currentSection.appendCodePoint(codePoint);
            return true;
        });

        if (!currentSection.isEmpty()) {
            text.append(Text.literal(currentSection.toString()).setStyle(currentStyle.get()));
        }

        return text;
    }

    public static Text fromStringVisitable(StringVisitable stringVisitable) {
        if (stringVisitable instanceof Text) return (Text) stringVisitable;

        MutableText text = Text.empty();
        StringBuilder currentSection = new StringBuilder();
        AtomicReference<Style> currentStyle = new AtomicReference<>();

        stringVisitable.visit((style, string) -> {
            if (currentStyle.get() == null) {
                currentStyle.set(style);
            } else if (currentStyle.get() != style) {
                if (!currentSection.isEmpty()) {
                    text.append(Text.literal(currentSection.toString()).setStyle(currentStyle.get()));
                    currentSection.setLength(0);
                }
                currentStyle.set(style);
            }
            currentSection.append(string);
            return Optional.empty();
        }, Style.EMPTY);

        if (!currentSection.isEmpty()) {
            text.append(Text.literal(currentSection.toString()).setStyle(currentStyle.get()));
        }

        return text;
    }
}
