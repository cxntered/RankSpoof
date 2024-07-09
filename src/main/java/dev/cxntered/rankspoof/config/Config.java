package dev.cxntered.rankspoof.config;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;

public class Config extends Vigilant {
    private static final String CONFIG_FILE_PATH = "./config/rankspoof.toml";
    private static final Config INSTANCE = new Config();

    public Config() {
        super(new File(CONFIG_FILE_PATH));
        initialize();

        addDependency("spoofedRank", "enabled");
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    @Property(
            type = PropertyType.SWITCH,
            name = "Enabled",
            description = "Whether or not the mod is enabled.",
            category = "General"
    )
    public boolean enabled = true;

    @Property(
            type = PropertyType.TEXT,
            name = "Spoofed Rank",
            description = "The rank to spoof. Use '&' for color codes.",
            category = "General"
    )
    public String spoofedRank = "&c[OWNER]";
}
