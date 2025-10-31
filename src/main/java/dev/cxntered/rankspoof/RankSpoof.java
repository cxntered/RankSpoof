package dev.cxntered.rankspoof;

import dev.cxntered.rankspoof.config.ModConfig;
import net.fabricmc.api.ModInitializer;

public class RankSpoof implements ModInitializer {
    @Override
    public void onInitialize() {
        ModConfig.CONFIG.load();
    }
}