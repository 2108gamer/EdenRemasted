package rip.diamond.practice.profile.menu;

import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.event.SettingsMenuOpenEvent;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.profile.menu.button.SettingsButton;
import rip.diamond.practice.profile.themes.Themes;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileSettingsMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return Language.PROFILE_SETTINGS_MENU_TITLE.toString();
    }
    ArrayList<String> lore = new ArrayList<>();


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        for (ProfileSettings settings : ProfileSettings.values()) {
            buttons.put(buttons.size(), new SettingsButton(settings));
        }

        SettingsMenuOpenEvent event = new SettingsMenuOpenEvent(player, this);
        event.call();




        return buttons;
    }
}
