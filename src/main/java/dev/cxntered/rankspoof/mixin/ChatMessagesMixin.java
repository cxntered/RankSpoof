package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.RankSpoof;
import dev.cxntered.rankspoof.config.ModConfig;
import dev.cxntered.rankspoof.text.LegacyFormatting;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.StringVisitable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChatMessages.class)
public abstract class ChatMessagesMixin {
    @ModifyArg(
            method = "breakRenderedChatMessageLines",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/font/TextHandler;wrapLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/text/Style;Ljava/util/function/BiConsumer;)V"
            ),
            index = 0
    )
    private static StringVisitable spoofChatMessageLine(StringVisitable stringVisitable) {
        if (ModConfig.CONFIG.instance().enabled) {
            String message = LegacyFormatting.toLegacy(stringVisitable);
            return LegacyFormatting.fromLegacy(RankSpoof.getSpoofedText(message));
        }
        return stringVisitable;
    }
}