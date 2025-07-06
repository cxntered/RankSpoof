package dev.cxntered.rankspoof.mixin.minecraft;

import dev.cxntered.rankspoof.RankSpoof;
import dev.cxntered.rankspoof.config.Config;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiUtilRenderComponents.class)
public abstract class MixinGuiUtilRenderComponents {
    @ModifyVariable(
            method = "splitText",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/util/IChatComponent;getUnformattedTextForChat()Ljava/lang/String;"
            )
    )
    private static String rankspoof$spoofSplitText(String string) {
        if (string == null) return null;
        if (Config.getInstance().enabled) return RankSpoof.getSpoofedText(string);
        return string;
    }
}
