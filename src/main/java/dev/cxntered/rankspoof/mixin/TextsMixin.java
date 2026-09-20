package dev.cxntered.rankspoof.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import dev.cxntered.rankspoof.RankSpoof;
import dev.cxntered.rankspoof.config.ModConfig;
import net.minecraft.client.util.Texts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Texts.class)
abstract class TextsMixin {
    @Definition(id = "computeValue", method = "Lnet/minecraft/text/Text;computeValue()Ljava/lang/String;")
    @Expression("? = ?.computeValue()")
    @ModifyVariable(method = "wrapLines", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
    private static String spoofWrapLines(String string) {
        if (string == null) return null;
        if (ModConfig.enabled.get()) return RankSpoof.getSpoofedText(string);
        return string;
    }
}
