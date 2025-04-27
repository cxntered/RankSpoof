package dev.cxntered.rankspoof;

import dev.cxntered.rankspoof.config.Config;
import net.fabricmc.api.ModInitializer;

public class RankSpoof implements ModInitializer {
    @Override
    public void onInitialize() {
        Config.CONFIG.load();
    }
}