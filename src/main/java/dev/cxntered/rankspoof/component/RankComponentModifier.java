package dev.cxntered.rankspoof.component;

import dev.cxntered.rankspoof.config.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;

import java.util.List;
import java.util.regex.Pattern;

public class RankComponentModifier {
    private static final Pattern RANK_START_PATTERN = Pattern.compile("\\[[A-Za-z]+"); // matches rank start, e.g. "[MVP" (for "[MVP", "++", "]") or "[VIP]"

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

        String rank = ModConfig.CONFIG.instance().spoofedRank.replace('&', '§');
        Component spoofedRankWithName = ComponentConverter.fromLegacyFormatting(rank + " " + username);
        List<Component> siblings = component.getSiblings();
        MutableComponent result = MutableComponent.create(component.getContents()).setStyle(component.getStyle());

        for (int i = 0; i < siblings.size(); i++) {
            Component sibling = siblings.get(i);
            String string = sibling.getString();

            if (RANK_START_PATTERN.matcher(string).find()) {
                // player has a rank
                int processed = findRankEndOffset(siblings, i, username);
                if (processed >= 0) {
                    result.append(spoofedRankWithName);
                    i += processed;
                    continue;
                }
            } else if (string.contains(username)) {
                if (isUnrankedPlayer(sibling, component)) {
                    // player has no rank
                    replaceUsername(result, string, username, sibling.getStyle(), spoofedRankWithName);
                    continue;
                } else if (isPlayerInTeam(siblings, i)) {
                    return component; // player is in a team, skip processing
                } else {
                    // rank prefix is omitted; append username with spoofed rank style only
                    Style rankStyle = spoofedRankWithName.getSiblings().getLast().getStyle();
                    replaceUsername(result, string, username, sibling.getStyle(), Component.literal(username).setStyle(rankStyle));
                    continue;
                }
            }

            result.append(sibling);
        }

        return result;
    }

    /**
     * Finds how many siblings after index <code>i</code> are part of a rank and should be skipped when replacing.
     * @return The number of siblings to skip or -1 if no valid rank pattern found.
     */
    private static int findRankEndOffset(List<Component> siblings, int i, String username) {
        Component sibling = siblings.get(i);

        // rank is contained within a single sibling, e.g. "[VIP]" or "[MVP]"
        if (sibling.getString().endsWith("] ") && i + 1 < siblings.size() && siblings.get(i + 1).getString().equals(username)) {
            return 1; // skip username sibling
        } else if (sibling.getString().endsWith("] " + username)) {
            return 0; // rank and username are in the same sibling
        }

        // rank spans multiple siblings, e.g. "[MVP", "++", "]" or "[", "YOUTUBE", "]"
        if (i + 3 < siblings.size() && siblings.get(i + 3).getString().equals(username)) {
            return 3; // skip rank parts and username
        } else if (i + 2 < siblings.size() && siblings.get(i + 2).getString().endsWith("] " + username)) {
            // username is in the same sibling as last rank part
            return 2; // skip rank parts and username
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
}
