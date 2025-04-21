package dev.cxntered.rankspoof;

import dev.cxntered.rankspoof.command.RankSpoofCommand;
import dev.cxntered.rankspoof.config.Config;
import gg.essential.universal.UMinecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(modid = "rankspoof", useMetadata = true)
public class RankSpoof {
    private static final Pattern TEAM_REGEX = Pattern.compile("(?:§r)?§[a-z0-9]§l[A-Z](?:§r)? .*"); // matches team prefixes, e.g. "§r§c§lR"
    private static final Pattern LAST_FORMAT_PATTERN = Pattern.compile("[§a-f0-9rblomn]{2}"); // matches last format, e.g. "§b" or "§l"

    private static String cachedUsername;
    private static Pattern rankPattern;
    private static Pattern noRankPattern;
    private static Pattern playerPattern;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Config.getInstance().preload();
        ClientCommandHandler.instance.registerCommand(new RankSpoofCommand());
    }

    public static String getSpoofedText(String text) {
        String username = UMinecraft.getMinecraft().getSession().getProfile().getName();
        if (!text.contains(username) || TEAM_REGEX.matcher(text).find()) return text;

        if (!username.equals(cachedUsername)) {
            cachedUsername = username;
            updatePatterns(username);
        }

        String rank = Config.getInstance().spoofedRank.replace('&', '§');
        Matcher rankMatcher = rankPattern.matcher(text);
        Matcher noRankMatcher = noRankPattern.matcher(text);

        if (rankMatcher.find()) {
            return rankMatcher.replaceFirst(rank + " " + username);
        } else if (noRankMatcher.find()) {
            return noRankMatcher.replaceFirst(rank + " " + username);
        } else {
            Matcher lastFormatMatcher = LAST_FORMAT_PATTERN.matcher(rank);
            String lastFormat = "";

            while (lastFormatMatcher.find()) {
                lastFormat = lastFormatMatcher.group();
            }

            return playerPattern.matcher(text).replaceFirst(lastFormat + username);
        }
    }

    private static void updatePatterns(String username) {
        String quotedUsername = Pattern.quote(username);
        rankPattern = Pattern.compile("\\[[A-Za-z§0-9+]+] " + quotedUsername); // matches rank & username, e.g. "§b[MVP§3+§b] cxntered"
        noRankPattern = Pattern.compile("(§r§7|§7)" + quotedUsername); // matches username without rank, e.g. "§7cxntered"
        playerPattern = Pattern.compile("[a-f0-9§]{2}" + quotedUsername); // matches rank color, e.g. "§b[MVP§3+§b] cxntered" -> "§b"
    }
}
