package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.config.ModConfig;
import dev.cxntered.rankspoof.config.RankPreview;
import dev.cxntered.rankspoof.text.SpoofedOrderedText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextRenderer.class)
public abstract class TextRendererMixin {
    @Shadow
    public abstract int getWidth(OrderedText text);

    @ModifyVariable(
            method = "prepare(Lnet/minecraft/text/OrderedText;FFIZI)Lnet/minecraft/client/font/TextRenderer$GlyphDrawable;",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private OrderedText spoofPrepare(OrderedText orderedText) {
        if (ModConfig.CONFIG.instance().enabled && !RankPreview.isRendering) return new SpoofedOrderedText(orderedText);
        return orderedText;
    }

    @ModifyVariable(
            method = "getWidth(Lnet/minecraft/text/OrderedText;)I",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private OrderedText spoofGetWidthOrderedText(OrderedText orderedText) {
        if (ModConfig.CONFIG.instance().enabled && !RankPreview.isRendering) return new SpoofedOrderedText(orderedText);
        return orderedText;
    }

    @Inject(
            method = "getWidth(Lnet/minecraft/text/StringVisitable;)I",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void spoofGetWidthStringVisitable(StringVisitable stringVisitable, CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.CONFIG.instance().enabled && stringVisitable instanceof Text text)
            cir.setReturnValue(this.getWidth(new SpoofedOrderedText(text.asOrderedText())));
    }
}
