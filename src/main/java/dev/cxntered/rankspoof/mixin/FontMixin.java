package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.config.ModConfig;
import dev.cxntered.rankspoof.config.RankPreview;
import dev.cxntered.rankspoof.component.ComponentConverter;
import dev.cxntered.rankspoof.component.RankComponentModifier;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("ModifyVariableMayUseName") // suppress while we still support unobfuscated
@Mixin(Font.class)
abstract class FontMixin {
    @ModifyVariable(
            //? if >=1.21.11 {
            method = "prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;",
            //?} else
            /*method = "prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZI)Lnet/minecraft/client/gui/Font$PreparedText;",*/
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private FormattedCharSequence spoofPrepareText(FormattedCharSequence text) {
        if (ModConfig.CONFIG.instance().enabled && !RankPreview.isRendering)
            return RankComponentModifier.replaceRank(ComponentConverter.fromFormattedCharSequence(text)).getVisualOrderText();
        return text;
    }

    @ModifyVariable(
            method = "width(Lnet/minecraft/util/FormattedCharSequence;)I",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private FormattedCharSequence spoofWidth$FormattedCharSequence(FormattedCharSequence text) {
        if (ModConfig.CONFIG.instance().enabled && !RankPreview.isRendering)
            return RankComponentModifier.replaceRank(ComponentConverter.fromFormattedCharSequence(text)).getVisualOrderText();
        return text;
    }

    @ModifyVariable(
            method = "width(Lnet/minecraft/network/chat/FormattedText;)I",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private FormattedText spoofWidth$FormattedText(FormattedText text) {
        if (ModConfig.CONFIG.instance().enabled && !RankPreview.isRendering)
            return RankComponentModifier.replaceRank(ComponentConverter.fromFormattedText(text));
        return text;
    }
}
