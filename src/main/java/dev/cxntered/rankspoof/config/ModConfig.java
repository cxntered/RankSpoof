package dev.cxntered.rankspoof.config;

import dev.cxntered.rankspoof.RankSpoof;
import dev.cxntered.rankspoof.component.ComponentConverter;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.Locale;

public class ModConfig {
    public static final ConfigClassHandler<ModConfig> CONFIG = ConfigClassHandler.createBuilder(ModConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve(RankSpoof.ID + ".json"))
                    .build())
            .build();

    @SerialEntry public boolean enabled = true;
    @SerialEntry public String spoofedRank = "&c[&6ዞ&c]";

    private static Component rankWithUsername = Component.empty();
    private static Component rankDisplay = Component.empty();
    private static String cachedUsername;

    public static Component getRankWithUsername() {
        String currentName = Minecraft.getInstance().getUser().getName();
        if (!currentName.equals(cachedUsername)) {
            cachedUsername = currentName;
            updateRankComponents();
        }
        return rankWithUsername;
    }

    public static Component getRankDisplay() {
        return rankDisplay;
    }

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Component.literal(RankSpoof.NAME))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Settings"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Enabled"))
                                .description(OptionDescription.of(Component.literal("Enable or disable the mod.")))
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
                                .binding(defaults.spoofedRank, () -> config.spoofedRank, newVal -> {
                                    config.spoofedRank = newVal;
                                    updateRankComponents();
                                })
                                .addListener((option, value) -> {
                                    RankPreview.spoofedRank = option.pendingValue();
                                })
                                .controller(StringControllerBuilder::create)
                                .build())
                        .build())
        )).generateScreen(parent);
    }

    public static void updateRankComponents() {
        String rank = CONFIG.instance().spoofedRank
                .replace("&&", "\u0000")
                .replace('&', '§')
                .replace("\u0000", "&");

        rankWithUsername = ComponentConverter.fromLegacyFormatting(rank + " " + cachedUsername);
        int open = rank.indexOf('[');
        int close = rank.lastIndexOf(']');
        rankDisplay = ComponentConverter.fromLegacyFormatting(
                // this only removes surrounding square brackets, could be changed to remove other brackets if needed
                open >= 0 && close > open
                        ? rank.substring(0, open) + rank.substring(open + 1, close) + rank.substring(close + 1)
                        : rank
        );
    }

    private static Component buildColorCodesDescription() {
        MutableComponent description = Component.literal("§lColor Codes§r (hover for info):\n");
        int count = 0;

        description.append(
                Component.literal("§7#§cRR§aGG§9BB\n")
                        .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Component.literal("RGB Color (Hex Code)"))))
        );
        for (ChatFormatting formatting : ChatFormatting.values()) {
            if (formatting.ordinal() < ChatFormatting.OBFUSCATED.ordinal()) {
                description.append(createFormattingDisplay(formatting));
                if (++count % 4 == 0) description.append(Component.literal("\n"));
            }
        }

        return description;
    }

    private static Component buildFormattingCodesDescription() {
        MutableComponent description = Component.literal("§lFormatting Codes§r (hover for info):\n");

        for (ChatFormatting formatting : ChatFormatting.values()) {
            if (formatting.ordinal() >= ChatFormatting.OBFUSCATED.ordinal()) {
                description.append(createFormattingDisplay(formatting));
                description.append(Component.literal("\n"));
            }
        }

        return description;
    }

    private static Component createFormattingDisplay(ChatFormatting formatting) {
        String name = toTitleCase(formatting.name());

        if (formatting.ordinal() < ChatFormatting.OBFUSCATED.ordinal()) {
            return Component.literal(formatting.toString() + formatting.toString().charAt(1) + " ")
                    .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Component.literal(name))));
        } else {
            return Component.literal(formatting.toString().charAt(1) + ": " + formatting + name + "§r")
                    .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Component.literal(name))));
        }
    }

    private static String toTitleCase(String input) {
        String[] words = input.toLowerCase(Locale.ROOT).split("_");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            if (!titleCase.isEmpty()) titleCase.append(" ");
            titleCase.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
        }
        return titleCase.toString();
    }
}
