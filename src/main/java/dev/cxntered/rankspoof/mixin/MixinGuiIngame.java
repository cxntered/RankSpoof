package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.config.Config;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame {
    @ModifyVariable(method = "renderScoreboard", at = @At(value = "STORE"), index = 15)
    private String rankspoof$spoofScoreboardRank(String string) {
        if (Config.getInstance().enabled && string.matches("Rank: .+")) {
            String rank = Config.getInstance().spoofedRank
                    .replace('&', '§')
                    .replace("[", "")
                    .replace("]", "");
            string = "Rank: " + rank;
        }

        return string;
    }
}
