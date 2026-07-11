package dev.cxntered.rankspoof.component;

import dev.cxntered.rankspoof.config.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;

import java.util.List;
import java.util.regex.Pattern;

public class RankComponentModifier {
    // matches rank start, e.g. "[MVP" (for "[MVP", "++", "]") or "[VIP]"
    private static final Pattern RANK_START_PATTERN = Pattern.compile("\\[[A-Za-z]+");

    /**
     * Replaces the rank in a Component with a spoofed rank.
     * <p>
     * As this mod is intended for Hypixel, it makes a few assumptions which may not hold true for other servers:
     * <ul>
     *  <li>The parent component's content is empty (i.e. all components will be in siblings)</li>
     *  <li>Siblings of the parent component will not have siblings of their own</li>
     *  <li>Ranks will always have either one or three parts (e.g. "[VIP]" or "[MVP", "++", "]")</li>
     *  <li>Legacy formatting will not be used</li>
     * </ul>
     */
    public static Component replaceRank(Component component) {
        String username = Minecraft.getInstance().getUser().getName();
        if (!component.getString().contains(username)) return component;

        List<Component> siblings = component.getSiblings();
        MutableComponent result = component.plainCopy().setStyle(component.getStyle());

        for (int i = 0; i < siblings.size(); i++) {
            Component sibling = siblings.get(i);
            Style style = sibling.getStyle();
            String string = sibling.getString();

            // player has a rank & text includes rank prefix
            if (RANK_START_PATTERN.matcher(string).find()) {
                int rankEndOffset = findRankEndOffset(siblings, i, username);
                if (rankEndOffset >= 0) {
                    result.append(applyInheritedStyle(ModConfig.getRankWithUsername(), style));

                    // add a space after the username for any tags (guild tag, housing rank, etc.)
                    Component lastRankComponent = siblings.get(i + rankEndOffset);
                    if (lastRankComponent.getString().endsWith(username + " ")) {
                        result.append(Component.literal(" ").setStyle(lastRankComponent.getStyle()));
                    }

                    i += rankEndOffset;
                    continue;
                }
            }

            // player doesn't have a rank or rank prefix is missing
            if (string.contains(username)) {
                if (isPlayerInTeam(siblings, i)) return component;

                if (isUnrankedPlayer(sibling, component)) {
                    replaceUsername(result, string, username, style, applyInheritedStyle(ModConfig.getRankWithUsername(), style));
                } else {
                    // rank prefix is omitted; append username with spoofed rank style only
                    Style rankStyle = ModConfig.getRankWithUsername().getSiblings().getLast().getStyle();
                    replaceUsername(result, string, username, style, Component.literal(username).setStyle(mergeStyle(rankStyle, style)));
                }
                continue;
            }

            result.append(sibling);
        }

        return result;
    }

    /**
     * Finds how many siblings after index <code>i</code> are part of a rank and should be skipped when replacing.
     * Slides a window of up to 4 siblings ahead (to be safe), looking for where the rank ends and the username appears.
     * @return The number of siblings to skip or -1 if no valid rank pattern found.
     */
    private static int findRankEndOffset(List<Component> siblings, int i, String username) {
        for (int offset = 0; offset < 4 && i + offset < siblings.size(); offset++) {
            String text = siblings.get(i + offset).getString();

            // don't slide past a sibling that starts a prefix (e.g. "[RED] " then "[VIP] ")
            if (offset > 0 && text.contains("[")) break;

            if (text.endsWith("] " + username) || text.endsWith("] " + username + " ")) {
                return offset; // username is in the same sibling as last rank part
            } else if (text.endsWith("] ") && i + offset + 1 < siblings.size()) {
                String nextText = siblings.get(i + offset + 1).getString();
                if (nextText.equals(username) || nextText.equals(username + " ")) {
                    return offset + 1; // username is in sibling after last rank part
                }
            }
        }

        return -1; // no rank found
    }

    private static void replaceUsername(MutableComponent result, String original, String username, Style style, Component replacement) {
        String[] parts = original.split(username, 2);
        result.append(Component.literal(parts[0]).setStyle(style));
        result.append(replacement);
        result.append(Component.literal(parts[1]).setStyle(style));
    }

    private static boolean isUnrankedPlayer(Component sibling, Component parent) {
        TextColor gray = TextColor.fromLegacyFormat(ChatFormatting.GRAY);
        return sibling.getStyle().getColor() == gray ||
                (sibling.getStyle().isEmpty() && parent.getStyle().getColor() == gray);
    }

    private static boolean isPlayerInTeam(List<Component> siblings, int currentIndex) {
        // team prefixes are single bold uppercase letters, e.g. "R", "B ", etc.
        int checkIndex;
        int requiredLength;

        if (currentIndex >= 2) {
            // team prefix is split into two siblings: the prefix and a space
            checkIndex = currentIndex - 2;
            requiredLength = 1;
        } else if (currentIndex == 1) {
            // team prefix is in a single sibling including the space
            checkIndex = 0;
            requiredLength = 2;
        } else {
            // username is the first sibling, no team prefix
            return false;
        }

        Component previousComponent = siblings.get(checkIndex);
        String content = previousComponent.getString();

        if (!previousComponent.getStyle().isBold() || content.length() != requiredLength) {
            return false;
        }

        char firstChar = content.charAt(0);
        return firstChar >= 'A' && firstChar <= 'Z';
    }

    /**
     * Applies non-formatting style properties (interactivity and font) from
     * {@code inherited} to each sibling of the given component.
     */
    private static Component applyInheritedStyle(Component component, Style inherited) {
        if (inherited.getClickEvent() == null && inherited.getHoverEvent() == null
                && inherited.getInsertion() == null && inherited.getFont().equals(FontDescription.DEFAULT)) {
            return component;
        }
        MutableComponent result = MutableComponent.create(component.getContents()).setStyle(mergeStyle(component.getStyle(), inherited));
        for (Component sibling : component.getSiblings()) {
            result.append(((MutableComponent) sibling).setStyle(mergeStyle(sibling.getStyle(), inherited)));
        }
        return result;
    }

    private static Style mergeStyle(Style target, Style inherited) {
        Style merged = target;
        if (inherited.getClickEvent() != null && merged.getClickEvent() == null)
            merged = merged.withClickEvent(inherited.getClickEvent());
        if (inherited.getHoverEvent() != null && merged.getHoverEvent() == null)
            merged = merged.withHoverEvent(inherited.getHoverEvent());
        if (inherited.getInsertion() != null && merged.getInsertion() == null)
            merged = merged.withInsertion(inherited.getInsertion());
        if (!inherited.getFont().equals(FontDescription.DEFAULT) && merged.getFont().equals(FontDescription.DEFAULT))
            merged = merged.withFont(inherited.getFont());
        return merged;
    }
}
