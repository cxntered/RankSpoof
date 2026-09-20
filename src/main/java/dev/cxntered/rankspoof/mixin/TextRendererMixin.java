package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.RankSpoof;
import dev.cxntered.rankspoof.config.ModConfig;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = TextRenderer.class, priority = 998)
abstract class TextRendererMixin {
    @ModifyVariable(method = "drawLayer(Ljava/lang/String;FFIZ)I", at = @At("HEAD"), argsOnly = true)
    private String spoofDrawLayer(String string) {
        if (string == null) return null;
        if (ModConfig.enabled.get()) return RankSpoof.getSpoofedText(string);
        return string;
    }

    @ModifyVariable(method = "getStringWidth", at = @At("HEAD"), argsOnly = true)
    private String spoofGetStringWidth(String string) {
        if (string == null) return null;
        if (ModConfig.enabled.get()) return RankSpoof.getSpoofedText(string);
        return string;
    }
}
