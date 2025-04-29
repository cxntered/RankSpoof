package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.config.ModConfig;
import dev.cxntered.rankspoof.text.SpoofedOrderedText;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
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
    private void spoofTooltipRank(OrderedText text, CallbackInfo ci) {
        if (ModConfig.CONFIG.instance().enabled) {
            String string = SpoofedOrderedText.getFormattedString(text);
            if (string.startsWith("§7Rank: ")) {
                String rank = ModConfig.CONFIG.instance().spoofedRank
                        .replace('&', '§')
                        .replace("[", "")
                        .replace("]", "");
                this.text = Text.of("§7Rank: §r" + rank).asOrderedText();
            }
        }
    }
}
