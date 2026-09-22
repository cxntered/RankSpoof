package dev.cxntered.rankspoof.component;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RankComponentModifierTest {
    // "§c[§6ዞ§c] cxntered"
    private static final Component SPOOFED_RANK_WITH_USERNAME = Component.empty()
            .append(Component.literal("[").withStyle(ChatFormatting.RED))
            .append(Component.literal("ዞ").withStyle(ChatFormatting.GOLD))
            .append(Component.literal("] cxntered").withStyle(ChatFormatting.RED));

    @Test
    void testReplaceMessageWithRank() {
        // "§b[MVP§3+§b] cxntered§f: hello world"
        var original = Component.empty()
                .append(Component.literal("[MVP").withStyle(ChatFormatting.AQUA))
                .append(Component.literal("+").withStyle(ChatFormatting.DARK_AQUA))
                .append(Component.literal("] cxntered").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(": hello world").withStyle(ChatFormatting.WHITE));

        // "§c[§6ዞ§c] cxntered§f: hello world"
        var expected = Component.empty()
                .append(SPOOFED_RANK_WITH_USERNAME)
                .append(Component.literal(": hello world").withStyle(ChatFormatting.WHITE));

        var actual = RankComponentModifier.replaceRank(original, "cxntered", SPOOFED_RANK_WITH_USERNAME);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testReplaceMessageWithNoRank() {
        // "§7cxntered§7: hello world"
        var original = Component.empty()
                .append(Component.literal("cxntered").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(": hello world").withStyle(ChatFormatting.GRAY));

        // "§c[§6ዞ§c] cxntered§7: hello world"
        var expected = Component.empty()
                .append(SPOOFED_RANK_WITH_USERNAME)
                .append(Component.literal(": hello world").withStyle(ChatFormatting.GRAY));

        var actual = RankComponentModifier.replaceRank(original, "cxntered", SPOOFED_RANK_WITH_USERNAME);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testReplaceUsernameWithTag() {
        var guildTag = Component.literal("[❤GREM❤]").withStyle(ChatFormatting.GOLD);

        // "§b[MVP§3+§b] cxntered §6[❤GREM❤]"
        var original = Component.empty()
                .append(Component.literal("[MVP").withStyle(ChatFormatting.AQUA))
                .append(Component.literal("+").withStyle(ChatFormatting.DARK_AQUA))
                .append(Component.literal("] cxntered ").withStyle(ChatFormatting.AQUA))
                .append(guildTag);

        // "§c[§6ዞ§c] cxntered§r §6[❤GREM❤]"
        var expected = Component.empty()
                .append(SPOOFED_RANK_WITH_USERNAME)
                .append(Component.literal(" "))
                .append(guildTag);

        var actual = RankComponentModifier.replaceRank(original, "cxntered", SPOOFED_RANK_WITH_USERNAME);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testReplaceUsernameWithColor() {
        var original = Component.empty().append(Component.literal("cxntered").withStyle(ChatFormatting.BLUE));
        var expected = Component.empty().append(Component.literal("cxntered").withStyle(ChatFormatting.RED));
        var actual = RankComponentModifier.replaceRank(original, "cxntered", SPOOFED_RANK_WITH_USERNAME);
        Assertions.assertEquals(expected, actual);
    }
}
