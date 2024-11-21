package rip.diamond.practice.misc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.misc.commands.PartnerMenus.SelecActionMenu;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class PartnerCommand extends Command {

    @CommandArgs(name = "partner", inGameOnly = false)
    @Override
    public void execute(CommandArguments command) {
        CommandSender sender = command.getSender();
        Player p = command.getPlayer();
        Party party = Party.getByPlayer(p);


        PlayerProfile profile = PlayerProfile.get(p);
        PlayerState state = profile.getPlayerState();

        if(state == PlayerState.IN_MATCH) {


            if(party != null && p.hasPermission("eden.extra.partner")) {

                    new SelecActionMenu().openMenu(p);


            }  else {
                Language.PARTY_NOT_IN_A_PARTY.sendMessage(p);
            }

        } else {
            p.sendMessage(ChatColor.RED + "Este comando solo puede ser usado en partidas");
        }



    }
}
