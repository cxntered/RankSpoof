package dev.cxntered.rankspoof;

import dev.cxntered.rankspoof.config.ModConfig;
import net.fabricmc.api.ModInitializer;

public class RankSpoof implements ModInitializer {
    public static final String ID = /*$ mod_id*/ "rankspoof";
    public static final String NAME = /*$ mod_name*/ "RankSpoof";

    @Override
    public void onInitialize() {
        ModConfig.CONFIG.load();
    }
}
