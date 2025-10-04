package dev.cxntered.rankspoof.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Text;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator;
import cc.polyfrost.oneconfig.config.migration.VigilanceName;

public class ModConfig extends Config {
    @VigilanceName(
            name = "Spoofed Rank",
            category = "General",
            subcategory = ""
    )
    @Text(
            name = "Spoofed Rank",
            description = "The rank to spoof. Use '&' for color codes.",
            size = OptionSize.DUAL
    )
    public String spoofedRank = "&c[OWNER]";

    public ModConfig() {
        super(new Mod("RankSpoof", ModType.HYPIXEL, "/RankSpoof.svg", new VigilanceMigrator("./rankspoof.toml")), "rankspoof.json");
        initialize();
    }
}
