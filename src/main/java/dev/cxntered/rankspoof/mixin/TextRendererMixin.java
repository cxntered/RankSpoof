package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.config.ModConfig;
import dev.cxntered.rankspoof.config.RankPreview;
import dev.cxntered.rankspoof.text.TextConverter;
import dev.cxntered.rankspoof.text.RankTextModifier;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextRenderer.class)
public abstract class TextRendererMixin {
    @ModifyVariable(
            method = "prepare(Lnet/minecraft/text/OrderedText;FFIZI)Lnet/minecraft/client/font/TextRenderer$GlyphDrawable;",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private OrderedText spoofPrepare(OrderedText orderedText) {
        if (ModConfig.CONFIG.instance().enabled && !RankPreview.isRendering)
            return RankTextModifier.replaceRank(TextConverter.fromOrderedText(orderedText)).asOrderedText();
        return orderedText;
    }

    @ModifyVariable(
            method = "getWidth(Lnet/minecraft/text/OrderedText;)I",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private OrderedText spoofGetWidthOrderedText(OrderedText orderedText) {
        if (ModConfig.CONFIG.instance().enabled && !RankPreview.isRendering)
            return RankTextModifier.replaceRank(TextConverter.fromOrderedText(orderedText)).asOrderedText();
        return orderedText;
    }

    @ModifyVariable(
            method = "getWidth(Lnet/minecraft/text/StringVisitable;)I",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private StringVisitable spoofGetWidthStringVisitable(StringVisitable stringVisitable) {
        if (ModConfig.CONFIG.instance().enabled && !RankPreview.isRendering)
            return RankTextModifier.replaceRank(TextConverter.fromStringVisitable(stringVisitable));
        return stringVisitable;
    }
}
