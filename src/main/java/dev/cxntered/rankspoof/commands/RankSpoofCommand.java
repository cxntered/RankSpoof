package dev.cxntered.rankspoof.commands;

import dev.cxntered.rankspoof.RankSpoof;
import dev.cxntered.rankspoof.config.Config;
import gg.essential.universal.UScreen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class RankSpoofCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "rankspoof";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.getCommandName();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        RankSpoof.screenToOpenNextTick = Config.getInstance().gui();
    }
}
