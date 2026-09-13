package dev.cxntered.rankspoof.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.cxntered.rankspoof.config.ModConfig;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.AbstractTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {
    @WrapOperation(method = "renderScoreboardObjective", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Team;decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Ljava/lang/String;)Ljava/lang/String;"))
    private String rankspoof$spoofScoreboardRank(AbstractTeam abstractTeam, String string, Operation<String> original) {
        String formattedString = original.call(abstractTeam, string);

        if (ModConfig.enabled.get() && formattedString.startsWith("Rank: ")) {
            String rank = ModConfig.spoofedRank.get()
                    .replace('&', '§')
                    .replace("[", "")
                    .replace("]", "");
            return "Rank: " + rank;
        }

        return formattedString;
    }
}
