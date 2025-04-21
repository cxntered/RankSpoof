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
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Config.getInstance().preload();
        ClientCommandHandler.instance.registerCommand(new RankSpoofCommand());
    }

    public static String getSpoofedText(String text) {
        String username = UMinecraft.getMinecraft().getSession().getProfile().getName();
        if (!text.contains(username) || text.matches("(?:§r)?§[a-z0-9]§l[A-Z](?:§r)? .*")) return text;

        String rank = Config.getInstance().spoofedRank.replaceAll("&", "§");

        String rankRegex = "\\[[A-Za-z§0-9+]+] " + Pattern.quote(username);
        Pattern rankPattern = Pattern.compile(rankRegex);
        Matcher rankMatcher = rankPattern.matcher(text);

        String noRankRegex = "(§r§7|§7)" + Pattern.quote(username);
        Pattern noRankPattern = Pattern.compile(noRankRegex);
        Matcher noRankMatcher = noRankPattern.matcher(text);

        if (rankMatcher.find()) {
            text = text.replaceAll(rankRegex, rank + " " + username);
        } else if (noRankMatcher.find()) {
            text = text.replaceAll(noRankRegex, rank + " " + username);
        } else {
            Pattern lastFormatPattern = Pattern.compile("[§a-f0-9rblomn]{2}");
            Matcher lastFormatMatcher = lastFormatPattern.matcher(rank);

            String lastFormat = "";
            while (lastFormatMatcher.find()) {
                lastFormat = lastFormatMatcher.group();
            }

            String playerRegex = "[a-f0-9§]{2}" + Pattern.quote(username);
            text = text.replaceAll(playerRegex, lastFormat + username);
        }

        return text;
    }
}
