package dev.cxntered.rankspoof.mixin.compatibility;

import dev.cxntered.rankspoof.RankSpoof;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "org.polyfrost.vanillahud.hud.Scoreboard$ScoreboardHUD", remap = false)
public abstract class MixinScoreboard_VanillaHUD {
    @Dynamic("VanillaHUD")
    @Redirect(
            method = "renderObjective",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/scoreboard/ScorePlayerTeam;formatPlayerName(Lnet/minecraft/scoreboard/Team;Ljava/lang/String;)Ljava/lang/String;",
                    remap = true
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
