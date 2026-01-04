package dev.cxntered.rankspoof.component;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ComponentConverter {
    public static Component fromFormattedCharSequence(FormattedCharSequence formattedCharSequence) {
        MutableComponent component = Component.empty();
        StringBuilder buffer = new StringBuilder();
        AtomicReference<Style> currentStyle = new AtomicReference<>();

        formattedCharSequence.accept((index, style, codePoint) -> {
            if (currentStyle.get() == null) {
                currentStyle.set(style);
            } else if (currentStyle.get() != style) {
                if (!buffer.isEmpty()) {
                    component.append(Component.literal(buffer.toString()).setStyle(currentStyle.get()));
                    buffer.setLength(0);
                }
                currentStyle.set(style);
            }
            buffer.appendCodePoint(codePoint);
            return true;
        });

        if (!buffer.isEmpty()) {
            component.append(Component.literal(buffer.toString()).setStyle(currentStyle.get()));
        }

        return component;
    }

    public static Component fromFormattedText(FormattedText formattedText) {
        if (formattedText instanceof Component) return (Component) formattedText;

        MutableComponent component = Component.empty();
        StringBuilder buffer = new StringBuilder();
        AtomicReference<Style> currentStyle = new AtomicReference<>();

        formattedText.visit((style, string) -> {
            if (currentStyle.get() == null) {
                currentStyle.set(style);
            } else if (currentStyle.get() != style) {
                if (!buffer.isEmpty()) {
                    component.append(Component.literal(buffer.toString()).setStyle(currentStyle.get()));
                    buffer.setLength(0);
                }
                currentStyle.set(style);
            }
            buffer.append(string);
            return Optional.empty();
        }, Style.EMPTY);

        if (!buffer.isEmpty()) {
            component.append(Component.literal(buffer.toString()).setStyle(currentStyle.get()));
        }

        return component;
    }

    public static Component fromLegacyFormatting(String string) {
        if (string == null || string.isEmpty()) {
            return Component.empty();
        }

        MutableComponent text = Component.empty();
        StringBuilder currentSegment = new StringBuilder();
        Style currentStyle = Style.EMPTY;

        for (int i = 0; i < string.length(); i++) {
            char currentChar = string.charAt(i);

            if (currentChar == '§' && i + 1 < string.length()) {
                if (!currentSegment.isEmpty()) {
                    text.append(Component.literal(currentSegment.toString()).setStyle(currentStyle));
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

                ChatFormatting format = ChatFormatting.getByCode(code);
                if (format != null) {
                    currentStyle = format == ChatFormatting.RESET
                            ? Style.EMPTY
                            : currentStyle.applyLegacyFormat(format);
                }
            } else {
                currentSegment.append(currentChar);
            }
        }

        if (!currentSegment.isEmpty()) {
            text.append(Component.literal(currentSegment.toString()).setStyle(currentStyle));
        }

        return text;
    }
}
