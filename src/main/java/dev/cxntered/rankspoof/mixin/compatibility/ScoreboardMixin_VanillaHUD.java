package dev.cxntered.rankspoof.mixin.compatibility;

import dev.cxntered.rankspoof.config.Config;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "org.polyfrost.vanillahud.hud.Scoreboard$ScoreboardHUD", remap = false)
public abstract class ScoreboardMixin_VanillaHUD {
    @Unique
    private String rankspoof$getRank() {
        return Config.getInstance().spoofedRank
                .replace('&', '§')
                .replace("[", "")
                .replace("]", "");
    }

    @Dynamic("VanillaHUD")
    @Redirect(
            method = "renderObjective",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/scoreboard/ScorePlayerTeam;formatPlayerName(Lnet/minecraft/scoreboard/Team;Ljava/lang/String;)Ljava/lang/String;",
                    ordinal = 0,
                    remap = true
            )
    )
    private String rankspoof$spoofScoreboardRankWidth(Team team, String string) {
        String scoreboardLine = ScorePlayerTeam.formatPlayerName(team, string);

        if (Config.getInstance().enabled && scoreboardLine.matches("Rank: .+")) {
            return "Rank: " + rankspoof$getRank();
        }

        return scoreboardLine;
    }

    @Dynamic("VanillaHUD")
    @Redirect(
            method = "renderObjective",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/scoreboard/ScorePlayerTeam;formatPlayerName(Lnet/minecraft/scoreboard/Team;Ljava/lang/String;)Ljava/lang/String;",
                    ordinal = 1,
                    remap = true
            )
    )
    private String rankspoof$spoofScoreboardRank(Team team, String string) {
        String scoreboardLine = ScorePlayerTeam.formatPlayerName(team, string);

        if (Config.getInstance().enabled && scoreboardLine.matches("Rank: .+")) {
            return "Rank: " + rankspoof$getRank();
        }

        return scoreboardLine;
    }
}
