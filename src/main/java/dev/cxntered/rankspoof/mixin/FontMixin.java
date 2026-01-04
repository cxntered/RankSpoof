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

@Mixin(Font.class)
public abstract class FontMixin {
    @ModifyVariable(
            //? if <=1.21.5 {
            /*method = "renderText(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;IIZ)F",
            *///?} elif <=1.21.10 {
            /*method = "prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZI)Lnet/minecraft/client/gui/Font$PreparedText;",
            *///?} else
            method = "prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private FormattedCharSequence spoofPrepareText(FormattedCharSequence formattedCharSequence) {
        if (ModConfig.CONFIG.instance().enabled && !RankPreview.isRendering)
            return RankComponentModifier.replaceRank(ComponentConverter.fromFormattedCharSequence(formattedCharSequence)).getVisualOrderText();
        return formattedCharSequence;
    }

    @ModifyVariable(
            method = "width(Lnet/minecraft/util/FormattedCharSequence;)I",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private FormattedCharSequence spoofWidth$FormattedCharSequence(FormattedCharSequence formattedCharSequence) {
        if (ModConfig.CONFIG.instance().enabled && !RankPreview.isRendering)
            return RankComponentModifier.replaceRank(ComponentConverter.fromFormattedCharSequence(formattedCharSequence)).getVisualOrderText();
        return formattedCharSequence;
    }

    @ModifyVariable(
            method = "width(Lnet/minecraft/network/chat/FormattedText;)I",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private FormattedText spoofWidth$FormattedText(FormattedText formattedText) {
        if (ModConfig.CONFIG.instance().enabled && !RankPreview.isRendering)
            return RankComponentModifier.replaceRank(ComponentConverter.fromFormattedText(formattedText));
        return formattedText;
    }
}
