package dev.cxntered.rankspoof.mixin.minecraft;

import dev.cxntered.rankspoof.RankSpoof;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame {
    @Redirect(
            method = "renderScoreboard",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/scoreboard/ScorePlayerTeam;formatPlayerName(Lnet/minecraft/scoreboard/Team;Ljava/lang/String;)Ljava/lang/String;"
            )
    )
    private String rankspoof$spoofScoreboardRank(Team team, String string) {
        String formattedString = ScorePlayerTeam.formatPlayerName(team, string);

        if (RankSpoof.config.enabled && formattedString.startsWith("Rank: ")) {
            String rank = RankSpoof.config.spoofedRank
                    .replace('&', '§')
                    .replace("[", "")
                    .replace("]", "");
            return "Rank: " + rank;
        }

        return formattedString;
    }
}
