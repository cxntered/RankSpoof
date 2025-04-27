package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.text.SpoofedOrderedText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextRenderer.class)
public class TextRendererMixin {
    @ModifyVariable(
            method = "drawInternal(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)I",
            at = @At(value = "HEAD", ordinal = 0),
            argsOnly = true
    )
    private String spoofDrawInternalString(String string) {
        // TODO: implement spoofing
        return string;
    }

    @ModifyVariable(
            method = "drawLayer(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)F",
            at = @At(value = "HEAD", ordinal = 0),
            argsOnly = true
    )
    private OrderedText spoofDrawLayerOrderedText(OrderedText orderedText) {
        return new SpoofedOrderedText(orderedText);
    }

    @ModifyVariable(
            method = "getWidth(Ljava/lang/String;)I",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private String spoofGetWidthString(String string) {
        // TODO: implement spoofing
        return string;
    }

    @ModifyVariable(
            method = "getWidth(Lnet/minecraft/text/OrderedText;)I",
            at = @At(value = "HEAD", ordinal = 0),
            argsOnly = true
    )
    private OrderedText spoofGetWidthOrderedText(OrderedText orderedText) {
        return new SpoofedOrderedText(orderedText);
    }
}
