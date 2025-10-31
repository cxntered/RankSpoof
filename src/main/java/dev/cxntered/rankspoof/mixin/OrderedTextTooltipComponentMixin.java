package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.config.ModConfig;
import dev.cxntered.rankspoof.text.TextConverter;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OrderedTextTooltipComponent.class)
public abstract class OrderedTextTooltipComponentMixin {
    @Shadow
    @Final
    @Mutable
    private OrderedText text;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void spoofTooltipRank(OrderedText orderedText, CallbackInfo ci) {
        if (ModConfig.CONFIG.instance().enabled) {
            Text text = TextConverter.fromOrderedText(orderedText);
            if (text.getSiblings().isEmpty()) return;

            Text firstSibling = text.getSiblings().getFirst();
            if (firstSibling.getString().equals("Rank: ") && firstSibling.getStyle().getColor() == TextColor.fromFormatting(Formatting.GRAY)) {
                String rank = ModConfig.CONFIG.instance().spoofedRank
                        .replace('&', '§')
                        .replace("[", "")
                        .replace("]", "");
                this.text = Text.literal("§7Rank: §r").append(TextConverter.fromLegacyFormatting(rank)).asOrderedText();
            }
        }
    }
}
