package dev.cxntered.rankspoof.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModConfig {
    public static final ConfigClassHandler<ModConfig> CONFIG = ConfigClassHandler.createBuilder(ModConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("rankspoof.json"))
                    .build())
            .build();

    @SerialEntry
    public boolean enabled = true;
    @SerialEntry
    public String spoofedRank = "&c[OWNER]";

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Text.literal("RankSpoof"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Settings"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Enabled"))
                                .description(OptionDescription.of(Text.literal("Enable or disable the mod")))
                                .binding(defaults.enabled, () -> config.enabled, newVal -> config.enabled = newVal)
                                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true))
                                .build())
                        .option(Option.<String>createBuilder()
                                .name(Text.literal("Spoofed Rank"))
                                .description(
                                        OptionDescription.createBuilder()
                                                .text(Text.literal("The rank to spoof. Use '&' for color/formatting codes.")
                                                        .append(Text.literal("\n\n"))
                                                        .append(buildColorCodesDescription())
                                                        .append(Text.literal("\n"))
                                                        .append(buildFormattingCodesDescription()))
                                                .customImage(new RankPreview())
                                                .build()
                                )
                                .binding(defaults.spoofedRank, () -> config.spoofedRank, newVal -> config.spoofedRank = newVal)
                                .addListener((option, value) -> {
                                    RankPreview.rank = option.pendingValue();
                                })
                                .controller(StringControllerBuilder::create)
                                .build())
                        .build())
        )).generateScreen(parent);
    }

    private static Text buildColorCodesDescription() {
        MutableText description = Text.literal("§lAvailable color codes (hover for info):§r\n");
        int count = 0;

        for (Formatting formatting : Formatting.values()) {
            if (formatting.isColor()) {
                description.append(createFormattingDisplay(formatting));
                if (++count % 4 == 0) description.append(Text.literal("\n"));
            }
        }

        return description;
    }

    private static Text buildFormattingCodesDescription() {
        MutableText description = Text.literal("§lAvailable formatting codes (hover for info):§r\n");

        for (Formatting formatting : Formatting.values()) {
            if (!formatting.isColor()) {
                description.append(createFormattingDisplay(formatting));
                description.append(Text.literal("\n"));
            }
        }

        return description;
    }

    private static Text createFormattingDisplay(Formatting formatting) {
        String name = toTitleCase(formatting.getName());

        if (formatting.isColor()) {
            return Text.literal(formatting.toString() + formatting.getCode() + " ")
                    .setStyle(Style.EMPTY.withHoverEvent(
                            new HoverEvent.ShowText(Text.literal(name))));
        } else {
            return Text.literal(formatting.getCode() + ": " + formatting + name + "§r")
                    .setStyle(Style.EMPTY.withHoverEvent(
                            new HoverEvent.ShowText(Text.literal(name))));
        }
    }

    private static String toTitleCase(String input) {
        String[] words = input.toLowerCase().split("_");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            titleCase.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return titleCase.toString().trim();
    }
}
