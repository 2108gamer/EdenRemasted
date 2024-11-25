package rip.diamond.practice.misc.commands.PartnerMenus;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.party.PartyMember;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.*;

public class SelecActionMenu extends Menu {

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        PlayerProfile profile = PlayerProfile.get(player);
        ChatColor def = CC.getColorFromName(Config.DEFAULT_THEME.toString());
        Object theme = profile.getSettings().get(ProfileSettings.THEME_SELECTION);

        ChatColor c = CC.getColorFromName(theme.toString());

        Party party = Party.getByPlayer(player);
        if (party == null) {
            Cancel(player);
        }

        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {

                return new ItemBuilder(Material.ENCHANTED_BOOK)
                        .name("&b&lParty TP")
                        .lore(Language.PARTNER_TP_ALL_LORE.toStringList())
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                assert party != null;
                List<PartyMember> members = party.getAllPartyMembers();
                Location location = player.getLocation();
                teleportPartyMembers(members, location);
            }
        });

        buttons.put(12, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {

                Party party = Party.getByPlayer(player);
                boolean isFlyEnabled = checkFlyStatusForPartyMembers(player);

                String flyStatus = isFlyEnabled ? "» Activado" : "» Desactivado";

                List<String> loreList = Language.PARTNER_FLY_STATUS.toStringList();
                List<String> updatedLoreList = new ArrayList<>();
                for (String lore : loreList) {
                    String updatedLore = lore.replaceAll("%status%", flyStatus);
                    updatedLoreList.add(updatedLore);
                }

                return new ItemBuilder(Material.FEATHER)
                        .name("&b&lParty Fly")
                        .lore(updatedLoreList)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                Party party = Party.getByPlayer(player);
                List<PartyMember> members = party.getAllPartyMembers();
                toggleFlyForPartyMembers(members);
                openMenu(player);
            }
        });


        buttons.put(14, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {

                List<String> lore = Collections.singletonList(
                        Language.PARTNER_TERMINAR_MATCH.toString()
                );

                return new ItemBuilder(Material.BARRIER)
                        .name("&b&lTermina la partida")
                        .lore(Language.PARTNER_TERMINAR_MATCH.toStringList())
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
            PlayerProfile profile = PlayerProfile.get(player);
            Match match = profile.getMatch();

            if(match != null) {
                match.end(true, "&eEl partner " + player.getDisplayName() + " decidio terminar la partida");
            }
            }
        });

        buttons.put(16, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.LAVA_BUCKET)
                        .name("&b&lParty Kick")
                        .lore(Language.PARTNER_KICK_LORE.toStringList(player))
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
            new PartyMembersMenu().openMenu(player);
            }
        });

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 4;
    }

    @Override
    public String getTitle(Player player) {
        return Language.KIT_EDITOR_SAVE_MENU_NAME.toString();
    }

    public void teleportPartyMembers(List<PartyMember> members, Location location) {
        for (PartyMember member : members) {
            Player player = member.getPlayer();
            if (player != null && player.isOnline()) {
                player.teleport(location);
            }
        }
    }

    public void toggleFlyForPartyMembers(List<PartyMember> members) {
        for (PartyMember member : members) {
            Player player = member.getPlayer();
            if (player != null && player.isOnline()) {
                if (player.getAllowFlight()) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    player.sendMessage(Language.PARTNER_FLY_DISABLE_MESSAGE.toString());
                } else {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    player.sendMessage(Language.PARTNER_FLY_ENABLED_MESSAGE.toString());
                }
            }
        }
    }
    private boolean checkFlyStatusForPartyMembers(Player player) {
        Party party = Party.getByPlayer(player);
        boolean isFlyEnabled = false;
        List<PartyMember> members = party.getAllPartyMembers();

        for (PartyMember member : members) {
            Player partyPlayer = member.getPlayer();
            if (partyPlayer != null && partyPlayer.isOnline()) {
                if (partyPlayer.getAllowFlight()) {
                    isFlyEnabled = true;
                    break;
                }
            }
        }
        return isFlyEnabled;
    }



    public void Cancel(Player player) {
        Language.PARTY_NOT_IN_A_PARTY.sendMessage(player);
    }
}
