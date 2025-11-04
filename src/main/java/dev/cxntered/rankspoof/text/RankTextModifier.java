package dev.cxntered.rankspoof.text;

import dev.cxntered.rankspoof.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.regex.Pattern;

public class RankTextModifier {
    private static final Pattern RANK_START_PATTERN = Pattern.compile("\\[[A-Za-z]+"); // matches rank start, e.g. "[MVP" (for "[MVP", "++", "]") or "[VIP]"

    /**
     * Replaces the rank in a Text with a spoofed rank.
     * <p>
     * As this mod is intended for Hypixel, it makes a few assumptions which may not hold true for other servers:
     * <ul>
     *  <li>The parent text's content is empty (i.e. all text will be in siblings)</li>
     *  <li>Siblings of the parent text will not have siblings of their own</li>
     *  <li>Ranks will always have either one or three parts (e.g. "[VIP]" or "[MVP", "++", "]")</li>
     *  <li>Legacy formatting will not be used</li>
     * </ul>
     */
    public static Text replaceRank(Text text) {
        String username = MinecraftClient.getInstance().getSession().getUsername();
        if (!text.getString().contains(username)) return text;

        String rank = ModConfig.CONFIG.instance().spoofedRank.replace('&', '§');
        List<Text> siblings = text.getSiblings();
        MutableText result = MutableText.of(text.getContent()).setStyle(text.getStyle());

        for (int i = 0; i < siblings.size(); i++) {
            Text sibling = siblings.get(i);
            String string = sibling.getString();

            if (RANK_START_PATTERN.matcher(string).find()) {
                // player has a rank
                int processed = replaceRank(result, siblings, i, username, rank);
                if (processed != -1) {
                    i += processed;
                    continue;
                }
            } else if (string.contains(username)) {
                if (isUnrankedPlayer(sibling, text)) {
                    // player has no rank
                    replaceUsername(result, string, username, sibling.getStyle(), TextConverter.fromLegacyFormatting(rank + " " + username));
                    continue;
                } else if (isPlayerInTeam(siblings, i)) {
                    // player is in a team, skip processing text
                    return text;
                } else {
                    // rank prefix is omitted; append username with spoofed rank style only
                    Text rankText = TextConverter.fromLegacyFormatting(rank);
                    replaceUsername(result, string, username, sibling.getStyle(), Text.literal(username).setStyle(rankText.getSiblings().getLast().getStyle()));
                    continue;
                }
            }

            result.append(sibling);
        }

        return result;
    }

    private static int replaceRank(MutableText result, List<Text> siblings, int i, String username, String rank) {
        Text sibling = siblings.get(i);

        // rank is contained within a single sibling, e.g. "[VIP]" or "[MVP]"
        if (sibling.getString().endsWith("] ") && i + 1 < siblings.size() && siblings.get(i + 1).getString().equals(username)) {
            result.append(TextConverter.fromLegacyFormatting(rank + " " + username));
            return 1; // skip username sibling
        } else if (sibling.getString().endsWith("] " + username)) {
            // rank and username are in the same sibling
            result.append(TextConverter.fromLegacyFormatting(rank + " " + username));
            return 0;
        }

        // rank spans multiple siblings, e.g. "[MVP", "++", "]" or "[", "YOUTUBE", "]"
        if (i + 3 < siblings.size() && siblings.get(i + 3).getString().equals(username)) {
            result.append(TextConverter.fromLegacyFormatting(rank + " " + username));
            return 3; // skip rank parts and username
        } else if (i + 2 < siblings.size() && siblings.get(i + 2).getString().endsWith("] " + username)) {
            // username is in the same sibling as last rank part
            result.append(TextConverter.fromLegacyFormatting(rank + " " + username));
            return 2; // skip rank parts and username
        }

        return -1; // no replacement made
    }

    private static void replaceUsername(MutableText result, String original, String username, Style style, Text replacement) {
        String[] parts = original.split(username, 2);
        result.append(Text.literal(parts[0]).setStyle(style));
        result.append(replacement);
        result.append(Text.literal(parts[1]).setStyle(style));
    }

    private static boolean isUnrankedPlayer(Text sibling, Text parent) {
        TextColor gray = TextColor.fromFormatting(Formatting.GRAY);
        return sibling.getStyle().getColor() == gray ||
                (sibling.getStyle().isEmpty() && parent.getStyle().getColor() == gray);
    }

    private static boolean isPlayerInTeam(List<Text> siblings, int currentIndex) {
        int checkIndex = currentIndex >= 2 ? currentIndex - 2 : (currentIndex == 1 ? 0 : -1);
        int requiredLength = currentIndex >= 2 ? 1 : 2;

        if (checkIndex != -1) {
            Text prevText = siblings.get(checkIndex);
            String prevContent = prevText.getString();
            if (prevText.getStyle().isBold() && prevContent.length() == requiredLength) {
                char firstChar = prevContent.charAt(0);
                return firstChar >= 'A' && firstChar <= 'Z';
            }
        }

        return false;
    }
}
