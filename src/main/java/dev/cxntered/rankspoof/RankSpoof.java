package dev.cxntered.rankspoof;

import dev.cxntered.rankspoof.commands.RankSpoofCommand;
import dev.cxntered.rankspoof.config.Config;
import gg.essential.universal.UMinecraft;
import gg.essential.universal.UScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(modid = "rankspoof", useMetadata = true)
public class RankSpoof {
    public static Config config;
    public static GuiScreen screenToOpenNextTick = null;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        config = Config.getInstance();
        config.preload();

        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new RankSpoofCommand());
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        if (screenToOpenNextTick != null) {
            UScreen.displayScreen(screenToOpenNextTick);
            screenToOpenNextTick = null;
        }
    }

    public static String getSpoofedText(String text) {
        if (config == null)
            return text;

        String username = UMinecraft.getMinecraft().getSession().getProfile().getName();
        String rank = config.spoofedRank.replaceAll("&", "§");

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
