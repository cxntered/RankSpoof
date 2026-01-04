package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.config.ModConfig;
import dev.cxntered.rankspoof.component.ComponentConverter;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @ModifyVariable(
            method = "method_55439", // displayScoreboardSidebar$method_55439 (lambda)
            at = @At(value = "STORE"),
            index = 6
    )
    private Component spoofScoreboardRank(Component component) {
        if (ModConfig.CONFIG.instance().enabled && component.getString().startsWith("Rank: ")) {
            String rank = ModConfig.CONFIG.instance().spoofedRank
                    .replace('&', '§')
                    .replace("[", "")
                    .replace("]", "");
            return Component.literal("Rank: ").append(ComponentConverter.fromLegacyFormatting(rank));
        }
        return component;
    }
}
