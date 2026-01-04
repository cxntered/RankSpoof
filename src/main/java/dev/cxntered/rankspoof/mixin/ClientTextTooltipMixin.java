package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.config.ModConfig;
import dev.cxntered.rankspoof.component.ComponentConverter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientTextTooltip.class)
public abstract class ClientTextTooltipMixin {
    @Shadow
    @Final
    @Mutable
    private FormattedCharSequence text;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void spoofTooltipRank(FormattedCharSequence formattedCharSequence, CallbackInfo ci) {
        if (ModConfig.CONFIG.instance().enabled) {
            Component text = ComponentConverter.fromFormattedCharSequence(formattedCharSequence);
            if (text.getSiblings().isEmpty()) return;

            Component firstSibling = text.getSiblings().getFirst();
            if (firstSibling.getString().equals("Rank: ") && firstSibling.getStyle().getColor() == TextColor.fromLegacyFormat(ChatFormatting.GRAY)) {
                String rank = ModConfig.CONFIG.instance().spoofedRank
                        .replace('&', '§')
                        .replace("[", "")
                        .replace("]", "");
                this.text = Component.literal("§7Rank: §r").append(ComponentConverter.fromLegacyFormatting(rank)).getVisualOrderText();
            }
        }
    }
}
