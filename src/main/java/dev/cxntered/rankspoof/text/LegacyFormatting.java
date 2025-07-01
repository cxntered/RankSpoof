package dev.cxntered.rankspoof.text;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is used to convert to and from legacy formatting codes (i.e. codes starting with '§').
 */
public class LegacyFormatting {
    /**
     * Converts a Text object to a legacy formatted string.
     *
     * @param text The Text object to convert.
     * @return A string with legacy formatting.
     */
    public static String toLegacy(Text text) {
        List<Pair<String, Style>> components = new ArrayList<>();
        text.visit((style, string) -> {
            components.add(new Pair<>(string, style));
            return Optional.empty();
        }, Style.EMPTY);

        StringBuilder builder = new StringBuilder();
        Style lastFormatting = Style.EMPTY;

        for (Pair<String, Style> component : components) {
            String string = component.getLeft();
            Style style = component.getRight();

            String formatting = getFormattingCodes(style, lastFormatting);
            builder.append(formatting).append(string);

            lastFormatting = style;
        }

        return builder.toString();
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

        if (style.getColor() != null) {
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
     * @return True if a reset is needed, false if not.
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
