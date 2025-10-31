package dev.cxntered.rankspoof.text;

import dev.cxntered.rankspoof.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
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
        // TODO: clean up, extract into helper methods
        String username = MinecraftClient.getInstance().getSession().getUsername();
        String rank = ModConfig.CONFIG.instance().spoofedRank.replace('&', '§');
        if (!text.getString().contains(username)) return text;

        List<Text> siblings = text.getSiblings();
        MutableText mutableText = MutableText.of(text.getContent()).setStyle(text.getStyle());

        for (int i = 0; i < siblings.size(); i++) {
            Text sibling = siblings.get(i);
            String string = sibling.getString();

            if (RANK_START_PATTERN.matcher(string).find()) {
                // rank is contained within a single sibling, e.g. "[VIP]" or "[MVP]"
                if (sibling.getString().endsWith("] ") && i + 1 < siblings.size() && siblings.get(i + 1).getString().equals(username)) {
                    mutableText.append(TextConverter.fromLegacyFormatting(rank + " " + username));
                    i++; // skip username sibling
                    continue;
                } else if (sibling.getString().endsWith("] " + username)) {
                    // rank and username are in the same sibling
                    mutableText.append(TextConverter.fromLegacyFormatting(rank + " " + username));
                    continue;
                }

                // rank spans multiple siblings, e.g. "[MVP", "++", "]" or "[", "YOUTUBE", "]"
                if (i + 3 < siblings.size() && siblings.get(i + 3).getString().equals(username)) {
                    mutableText.append(TextConverter.fromLegacyFormatting(rank + " " + username));
                    i += 3; // skip rank parts and username
                    continue;
                } else if (i + 2 < siblings.size() && siblings.get(i + 2).getString().endsWith("] " + username)) {
                    // username is in the same sibling as last rank part
                    mutableText.append(TextConverter.fromLegacyFormatting(rank + " " + username));
                    i += 2; // skip rank parts and username
                    continue;
                }
            } else if (string.contains(username) && (sibling.getStyle().getColor() == TextColor.fromFormatting(Formatting.GRAY) || (sibling.getStyle().isEmpty() && text.getStyle().getColor() == TextColor.fromFormatting(Formatting.GRAY)))) {
                // player has no rank
                String[] parts = string.split(username, 2);
                mutableText.append(Text.literal(parts[0]).setStyle(sibling.getStyle()));
                mutableText.append(TextConverter.fromLegacyFormatting(rank + " " + username));
                mutableText.append(Text.literal(parts[1]).setStyle(sibling.getStyle()));
                continue;
            } else if (string.contains(username)) {
                int checkIndex = (i >= 2) ? i - 2 : (i == 1 ? 0 : -1);
                int requiredLength = (i >= 2) ? 1 : 2;
                if (checkIndex != -1) {
                    Text prevText = siblings.get(checkIndex);
                    String prevString = prevText.getString();
                    if (prevText.getStyle().isBold() && prevString.length() == requiredLength) {
                        char c = prevString.charAt(0);
                        if (c >= 'A' && c <= 'Z') return text;
                        // player is in a team, skip spoofing
                    }
                }

                // rank prefix is omitted, append username with spoofed rank style only
                Text rankText = TextConverter.fromLegacyFormatting(rank);
                String[] parts = string.split(username, 2);
                mutableText.append(Text.literal(parts[0]).setStyle(sibling.getStyle()));
                mutableText.append(Text.literal(username).setStyle(rankText.getSiblings().getLast().getStyle()));
                mutableText.append(Text.literal(parts[1]).setStyle(sibling.getStyle()));
                continue;
            }

            mutableText.append(sibling);
        }

        return mutableText;
    }

}
