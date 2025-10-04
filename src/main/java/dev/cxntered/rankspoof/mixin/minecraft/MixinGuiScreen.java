package dev.cxntered.rankspoof.mixin.minecraft;

import dev.cxntered.rankspoof.RankSpoof;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {
    @Redirect(
            method = "renderToolTip",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;set(ILjava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 1
            )
    )
    private Object rankspoof$spoofTooltipRank(List<String> list, int i, Object object) {
        if (RankSpoof.config.enabled) {
            if (!list.get(i).startsWith("§5§o§7Rank: ")) return object;

            String rank = RankSpoof.config.spoofedRank
                    .replace('&', '§')
                    .replace("[", "")
                    .replace("]", "");
            list.set(i, "§7Rank: " + rank);
        }

        return object;
    }
}
