package dev.cxntered.rankspoof.command;

import dev.cxntered.rankspoof.config.Config;
import gg.essential.api.EssentialAPI;
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
        EssentialAPI.getGuiUtil().openScreen(Config.getInstance().gui());
    }
}
