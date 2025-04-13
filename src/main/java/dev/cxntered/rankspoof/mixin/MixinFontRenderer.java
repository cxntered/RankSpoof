package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.RankSpoof;
import dev.cxntered.rankspoof.config.Config;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = FontRenderer.class, priority = 998)
public abstract class MixinFontRenderer {
    @ModifyVariable(method = "renderString", at = @At("HEAD"), argsOnly = true)
    private String rankspoof$spoofRenderString(String string) {
        if (string == null)
            return null;

        if (Config.getInstance().enabled)
            string = RankSpoof.getSpoofedText(string);

        return string;
    }

    @ModifyVariable(method = "getStringWidth", at = @At("HEAD"), argsOnly = true)
    private String rankspoof$spoofGetStringWidth(String string) {
        if (string == null)
            return null;

        if (Config.getInstance().enabled)
            string = RankSpoof.getSpoofedText(string);

        return string;
    }
}
