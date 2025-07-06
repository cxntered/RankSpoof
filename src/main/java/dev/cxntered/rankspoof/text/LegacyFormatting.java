package dev.cxntered.rankspoof.text;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is used to convert to and from legacy formatting codes (i.e. codes starting with '§').
 */
public class LegacyFormatting {
    /**
     * Converts a StringVisitable object to a legacy formatted string.
     *
     * @param stringVisitable The StringVisitable object to convert.
     * @return A string with legacy formatting.
     */
    public static String toLegacy(StringVisitable stringVisitable) {
        StringBuilder builder = new StringBuilder();
        AtomicReference<Style> lastFormatting = new AtomicReference<>(Style.EMPTY);

        stringVisitable.visit((style, string) -> {
            String formatting = getFormattingCodes(style, lastFormatting.get());
            builder.append(formatting).append(string);
            lastFormatting.set(style);
            return Optional.empty();
        }, Style.EMPTY);

        return builder.toString();
    }

    /**
     * Converts an OrderedText object to a legacy formatted string.
     *
     * @param orderedText The OrderedText object to convert.
     * @return A string with legacy formatting.
     */
    public static String toLegacy(OrderedText orderedText) {
        StringBuilder builder = new StringBuilder();
        AtomicReference<Style> lastFormatting = new AtomicReference<>(Style.EMPTY);

        orderedText.accept((index, style, codePoint) -> {
            String formatting = getFormattingCodes(style, lastFormatting.get());
            builder.append(formatting).append(Character.toString(codePoint));
            lastFormatting.set(style);
            return true;
        });

        return builder.toString();
    }

    /**
     * Converts a legacy formatted string to a StringVisitable object.
     *
     * @param string The legacy formatted string to convert.
     * @return A StringVisitable object with the equivalent formatting.
     */
    public static StringVisitable fromLegacy(String string) {
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

                Formatting format = Formatting.byCode(string.charAt(++i));
                if (format == null) continue;

                if (format == Formatting.RESET) {
                    currentStyle = Style.EMPTY;
                } else if (format.isColor()) {
                    currentStyle = currentStyle.withColor(format);
                } else {
                    currentStyle = switch (format) {
                        case BOLD -> currentStyle.withBold(true);
                        case ITALIC -> currentStyle.withItalic(true);
                        case UNDERLINE -> currentStyle.withUnderline(true);
                        case STRIKETHROUGH -> currentStyle.withStrikethrough(true);
                        case OBFUSCATED -> currentStyle.withObfuscated(true);
                        default -> currentStyle;
                    };
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

    /**
     * Converts a Style object to a string with legacy formatting codes.
     *
     * @param style          The Style object to convert.
     * @param lastFormatting The last formatting applied, used to determine if a reset is needed.
     * @return A string with legacy formatting codes.
     */
    private static String getFormattingCodes(Style style, Style lastFormatting) {
        if (style.equals(lastFormatting)) return "";

        StringBuilder formatting = new StringBuilder();

        if (needsReset(style, lastFormatting)) {
            formatting.append(Formatting.RESET);
        }

        if (style.getColor() != null && !style.getColor().equals(lastFormatting.getColor())) {
            Formatting colorCode = Formatting.byName(style.getColor().getName());
            if (colorCode != null) {
                formatting.append(colorCode);
            }
        }
        if (style.isBold()) formatting.append(Formatting.BOLD);
        if (style.isItalic()) formatting.append(Formatting.ITALIC);
        if (style.isUnderlined()) formatting.append(Formatting.UNDERLINE);
        if (style.isStrikethrough()) formatting.append(Formatting.STRIKETHROUGH);
        if (style.isObfuscated()) formatting.append(Formatting.OBFUSCATED);

        return formatting.toString();
    }

    /**
     * Checks if the current style needs a reset based on the last formatting applied.
     * <p>
     * This is because, in order to remove a formatting option, we need to reset all formatting first.
     *
     * @param style          The current style.
     * @param lastFormatting The last formatting applied.
     * @return {@code true} if a reset is needed, {@code false} otherwise.
     */
    private static boolean needsReset(Style style, Style lastFormatting) {
        // check if any formatting is removed
        if (lastFormatting.isBold() && !style.isBold()) return true;
        if (lastFormatting.isItalic() && !style.isItalic()) return true;
        if (lastFormatting.isUnderlined() && !style.isUnderlined()) return true;
        if (lastFormatting.isStrikethrough() && !style.isStrikethrough()) return true;
        if (lastFormatting.isObfuscated() && !style.isObfuscated()) return true;

        // check if color is being removed
        return lastFormatting.getColor() != null && style.getColor() == null;
    }
}
