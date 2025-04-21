package dev.cxntered.rankspoof.mixin;

import dev.cxntered.rankspoof.config.Config;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {
    @Inject(
            method = "renderToolTip",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;set(ILjava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 1, shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void rankspoof$spoofTooltipRank(ItemStack stack, int x, int y, CallbackInfo ci, List<String> list, int i) {
        if (Config.getInstance().enabled && stack.getDisplayName().equals("§aCharacter Information")) {
            Pattern pattern = Pattern.compile("§7Rank: .+");
            Matcher matcher = pattern.matcher(list.get(i));
            if (!matcher.find()) return;

            String rank = Config.getInstance().spoofedRank
                    .replace('&', '§')
                    .replace("[", "")
                    .replace("]", "");
            list.set(i, "§7Rank: " + rank);
        }
    }
}
