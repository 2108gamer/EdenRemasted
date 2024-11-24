package rip.diamond.practice.profile.divisions.command;

import org.bukkit.entity.Player;

import rip.diamond.practice.profile.divisions.menu.DivisionsMenu;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class DivisionMenuCommand extends Command {
    @CommandArgs(name = "division", inGameOnly = false)
    @Override
    public void execute(CommandArguments command) {
    Player p = command.getPlayer();

      new DivisionsMenu().openMenu(p);

    }
}
