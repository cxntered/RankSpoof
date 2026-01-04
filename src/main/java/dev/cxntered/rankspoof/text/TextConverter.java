package dev.cxntered.rankspoof.text;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class TextConverter {
    public static Text fromOrderedText(OrderedText orderedText) {
        MutableText text = Text.empty();
        StringBuilder buffer = new StringBuilder();
        AtomicReference<Style> currentStyle = new AtomicReference<>();

        orderedText.accept((index, style, codePoint) -> {
            if (currentStyle.get() == null) {
                currentStyle.set(style);
            } else if (currentStyle.get() != style) {
                if (!buffer.isEmpty()) {
                    text.append(Text.literal(buffer.toString()).setStyle(currentStyle.get()));
                    buffer.setLength(0);
                }
                currentStyle.set(style);
            }
            buffer.appendCodePoint(codePoint);
            return true;
        });

        if (!buffer.isEmpty()) {
            text.append(Text.literal(buffer.toString()).setStyle(currentStyle.get()));
        }

        return text;
    }

    public static Text fromStringVisitable(StringVisitable stringVisitable) {
        if (stringVisitable instanceof Text) return (Text) stringVisitable;

        MutableText text = Text.empty();
        StringBuilder buffer = new StringBuilder();
        AtomicReference<Style> currentStyle = new AtomicReference<>();

        stringVisitable.visit((style, string) -> {
            if (currentStyle.get() == null) {
                currentStyle.set(style);
            } else if (currentStyle.get() != style) {
                if (!buffer.isEmpty()) {
                    text.append(Text.literal(buffer.toString()).setStyle(currentStyle.get()));
                    buffer.setLength(0);
                }
                currentStyle.set(style);
            }
            buffer.append(string);
            return Optional.empty();
        }, Style.EMPTY);

        if (!buffer.isEmpty()) {
            text.append(Text.literal(buffer.toString()).setStyle(currentStyle.get()));
        }

        return text;
    }

    public static Text fromLegacyFormatting(String string) {
        if (string == null || string.isEmpty()) {
            return Text.empty();
        }

        MutableText text = Text.empty();
        StringBuilder currentSegment = new StringBuilder();
        Style currentStyle = Style.EMPTY;

        for (int i = 0; i < string.length(); i++) {
            char currentChar = string.charAt(i);

            if (currentChar == '§' && i + 1 < string.length()) {
                if (!currentSegment.isEmpty()) {
                    text.append(Text.literal(currentSegment.toString()).setStyle(currentStyle));
                    currentSegment.setLength(0);
                }

                char code = string.charAt(++i);

                // custom rgb color format (§#rrggbb)
                if (code == '#' && i + 6 < string.length()) {
                    String hexCode = string.substring(i + 1, i + 7);
                    try {
                        int color = Integer.parseInt(hexCode, 16);
                        currentStyle = currentStyle.withColor(TextColor.fromRgb(color));
                        i += 6;
                        continue;
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }

                Formatting format = Formatting.byCode(code);
                if (format != null) {
                    currentStyle = format == Formatting.RESET
                            ? Style.EMPTY
                            : currentStyle.withFormatting(format);
                }
            } else {
                currentSegment.append(currentChar);
            }
        }

        if (!currentSegment.isEmpty()) {
            text.append(Text.literal(currentSegment.toString()).setStyle(currentStyle));
        }

        return text;
    }
}
