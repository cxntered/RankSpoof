package dev.cxntered.rankspoof.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import dev.cxntered.rankspoof.RankSpoof;

@Command(value = "rankspoof", description = "Access the RankSpoof GUI.")
public class ModCommand {
    @Main
    private void handle() {
        RankSpoof.config.openGui();
    }
}