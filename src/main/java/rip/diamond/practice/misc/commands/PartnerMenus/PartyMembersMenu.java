package rip.diamond.practice.misc.commands.PartnerMenus;

import rip.diamond.practice.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.party.PartyMember;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartyMembersMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return Language.PARTY_MEMBERS_MENU_TITLE.toString();
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        Party party = Party.getByPlayer(player);
        if (party == null) {
            Language.PARTY_NOT_IN_A_PARTY.sendMessage(player);
            return buttons;
        }

        List<PartyMember> members = party.getAllPartyMembers();
        for (int i = 0; i < members.size(); i++) {
            PartyMember member = members.get(i);

            buttons.put(i, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.SKULL_ITEM)
                            .durability(3)
                            .headTexture(member.getUsername())
                            .name(member.getUsername())
                            .lore(Language.PARTY_MEMBERS_KICKED_BY_PARTNER_LORE.toString(), member.getUsername())

                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    if (member.getUniqueID().equals(player.getUniqueId())) {
                       player.sendMessage(ChatColor.RED + "No puedes expulsarte a ti mismo");
                        return;
                    }

                    Party party = Party.getByPlayer(player);
                    if (party != null) {
                        party.leave(member.getUniqueID(), true);
                    }


                    new PartyMembersMenu().openMenu(player);
                }
            });
        }

        return buttons;
    }
}
