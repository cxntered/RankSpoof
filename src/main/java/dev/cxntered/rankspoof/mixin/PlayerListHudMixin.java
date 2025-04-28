package dev.cxntered.rankspoof.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.cxntered.rankspoof.config.ModConfig;
import dev.cxntered.rankspoof.text.SpoofedOrderedText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"
            )
    )
    private int spoofGetWidth(TextRenderer textRenderer, StringVisitable stringVisitable, @Local(ordinal = 0) Text text) {
        if (ModConfig.CONFIG.instance().enabled)
            return textRenderer.getWidth(new SpoofedOrderedText(text.asOrderedText()));
        return textRenderer.getWidth(stringVisitable);
    }
}
