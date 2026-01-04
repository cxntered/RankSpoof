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
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class ModConfig {
    public static final ConfigClassHandler<ModConfig> CONFIG = ConfigClassHandler.createBuilder(ModConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("rankspoof.json"))
                    .build())
            .build();

    @SerialEntry
    public boolean enabled = true;
    @SerialEntry
    public String spoofedRank = "&c[&6ዞ&c]";

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Component.literal("RankSpoof"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Settings"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Enabled"))
                                .description(OptionDescription.of(Component.literal("Enable or disable the mod")))
                                .binding(defaults.enabled, () -> config.enabled, newVal -> config.enabled = newVal)
                                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true))
                                .build())
                        .option(Option.<String>createBuilder()
                                .name(Component.literal("Spoofed Rank"))
                                .description(
                                        OptionDescription.createBuilder()
                                                .text(Component.literal("The rank to spoof. Use '&' for color/formatting codes.")
                                                        .append(Component.literal("\n\n"))
                                                        .append(buildColorCodesDescription())
                                                        .append(Component.literal("\n"))
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

    private static Component buildColorCodesDescription() {
        MutableComponent description = Component.literal("§lAvailable color codes (hover for info)§r\n");
        int count = 0;

        description.append(Component.literal("#§cRR§aGG§9BB\n")
                .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Component.literal("RGB Color (Hex Code)"))))
        );
        for (ChatFormatting formatting : ChatFormatting.values()) {
            if (formatting.isColor()) {
                description.append(createFormattingDisplay(formatting));
                if (++count % 4 == 0) description.append(Component.literal("\n"));
            }
        }

        return description;
    }

    private static Component buildFormattingCodesDescription() {
        MutableComponent description = Component.literal("§lAvailable formatting codes (hover for info)§r\n");

        for (ChatFormatting formatting : ChatFormatting.values()) {
            if (!formatting.isColor()) {
                description.append(createFormattingDisplay(formatting));
                description.append(Component.literal("\n"));
            }
        }

        return description;
    }

    private static Component createFormattingDisplay(ChatFormatting formatting) {
        String name = toTitleCase(formatting.getName());

        if (formatting.isColor()) {
            return Component.literal(formatting.toString() + formatting.getChar() + " ")
                    .setStyle(Style.EMPTY.withHoverEvent(
                            new HoverEvent.ShowText(Component.literal(name))));
        } else {
            return Component.literal(formatting.getChar() + ": " + formatting + name + "§r")
                    .setStyle(Style.EMPTY.withHoverEvent(
                            new HoverEvent.ShowText(Component.literal(name))));
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
