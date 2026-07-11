package dev.cxntered.rankspoof.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.cxntered.rankspoof.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//~ if <26.2 'Hud' -> 'Gui'
@Mixin(net.minecraft.client.gui.Hud.class)
abstract class HudMixin {
    @WrapOperation(
            //~ if <26.1 'lambda$displayScoreboardSidebar$1' -> 'method_55439'
            method = "lambda$displayScoreboardSidebar$1",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/scores/PlayerTeam;formatNameForTeam(Lnet/minecraft/world/scores/Team;Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/MutableComponent;"
            )
    )
    private MutableComponent spoofScoreboardRank(Team team, Component name, Operation<MutableComponent> original) {
        MutableComponent component = original.call(team, name);

        if (ModConfig.CONFIG.instance().enabled && component.getString().startsWith("Rank: ")) {
            return Component.literal("Rank: ").append(ModConfig.getRankDisplay());
        }

        return component;
    }
}
