package dev.cxntered.rankspoof.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.cxntered.rankspoof.config.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Screen.class)
abstract class ScreenMixin {
    @WrapOperation(method = "renderTooltip(Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "INVOKE", target = "Ljava/util/List;set(ILjava/lang/Object;)Ljava/lang/Object;", ordinal = 1))
    private Object rankspoof$spoofTooltipRank(List<String> list, int i, Object object, Operation<Object> original, @Local(argsOnly = true) ItemStack stack) {
        if (ModConfig.enabled.get() && stack.getCustomName().equals("§aCharacter Information")) {
            if (!list.get(i).startsWith("§5§o§7Rank: ")) return original.call(list, i, object);

            String rank = ModConfig.spoofedRank.get()
                    .replace('&', '§')
                    .replace("[", "")
                    .replace("]", "");
            return original.call(list, i, "§7Rank: " + rank);
        }

        return original.call(list, i, object);
    }
}
