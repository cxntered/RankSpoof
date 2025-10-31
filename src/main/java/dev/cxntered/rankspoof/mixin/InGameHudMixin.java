package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.config.ModConfig;
import dev.cxntered.rankspoof.text.TextConverter;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @ModifyVariable(
            method = "method_55439", // renderScoreboardSidebar
            at = @At(value = "STORE"),
            index = 6
    )
    private Text spoofScoreboardRank(Text text) {
        if (ModConfig.CONFIG.instance().enabled && text.getString().startsWith("Rank: ")) {
            String rank = ModConfig.CONFIG.instance().spoofedRank
                    .replace('&', '§')
                    .replace("[", "")
                    .replace("]", "");
            return Text.literal("Rank: ").append(TextConverter.fromLegacyFormatting(rank));
        }
        return text;
    }
}
