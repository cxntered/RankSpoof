package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.config.ModConfig;
import dev.cxntered.rankspoof.component.RankComponentModifier;
import dev.cxntered.rankspoof.component.ComponentConverter;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ComponentRenderUtils.class)
abstract class ComponentRenderUtilsMixin {
    @ModifyArg(
            method = "wrapComponents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/StringSplitter;splitLines(Lnet/minecraft/network/chat/FormattedText;ILnet/minecraft/network/chat/Style;Ljava/util/function/BiConsumer;)V"
            ),
            index = 0
    )
    private static FormattedText spoofWrapComponents(FormattedText formattedText) {
        if (ModConfig.CONFIG.instance().enabled) {
            Component component = ComponentConverter.expandLegacyFormatting(ComponentConverter.fromFormattedText(formattedText));
            return RankComponentModifier.replaceRank(component);
        }
        return formattedText;
    }
}
