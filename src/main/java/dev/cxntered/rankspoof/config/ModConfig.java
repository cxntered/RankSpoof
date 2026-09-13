package dev.cxntered.rankspoof.config;

import net.ornithemc.osl.config.api.ConfigScope;
import net.ornithemc.osl.config.api.LoadingPhase;
import net.ornithemc.osl.config.api.config.BaseConfig;
import net.ornithemc.osl.config.api.config.option.BooleanOption;
import net.ornithemc.osl.config.api.config.option.StringOption;
import net.ornithemc.osl.config.api.serdes.FileSerializerType;
import net.ornithemc.osl.config.api.serdes.SerializerTypes;
import net.ornithemc.osl.core.api.json.JsonFile;

public class ModConfig extends BaseConfig {
    public static final FileSerializerType<JsonFile> SERIALIZER_TYPE = SerializerTypes.register("rankspoof:flat_json", JsonFile::new);

    public static final BooleanOption enabled = new BooleanOption("Enabled", "Enable or disable the mod.", true);
    public static final StringOption spoofedRank = new StringOption("Spoofed Rank", "The rank to spoof. Use '&' for color/formatting codes.", "&c[&6ዞ&c]");

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public String getName() {
        return "RankSpoof";
    }

    @Override
    public String getSaveName() {
        return "rankspoof.json";
    }

    @Override
    public ConfigScope getScope() {
        return ConfigScope.GLOBAL;
    }

    @Override
    public LoadingPhase getLoadingPhase() {
        return LoadingPhase.START;
    }

    @Override
    public FileSerializerType<?> getType() {
        return SERIALIZER_TYPE;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void init() {
        registerOptions("General", enabled, spoofedRank);
    }
}

