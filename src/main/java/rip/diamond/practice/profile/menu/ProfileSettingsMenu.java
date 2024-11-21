package rip.diamond.practice.profile.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.profile.menu.button.SettingsButton;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class ProfileSettingsMenu extends Menu {

    private static final Integer[] ALLOWED_SLOT = new Integer[]{
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    @Override
    public String getTitle(Player player) {
        return Language.PROFILE_SETTINGS_MENU_TITLE.toString();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        int index = 0;
        for (ProfileSettings settings : ProfileSettings.values()) {
            if (index < ALLOWED_SLOT.length) {
                buttons.put(ALLOWED_SLOT[index], new SettingsButton(settings));
                index++;
            }
        }


        for (int i = 0; i < 45; i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(Material.STAINED_GLASS_PANE)
                                .durability(7)
                                .name(" ")
                                .build();
                    }
                });
            }
        }

        return buttons;
    }

    @Override
    public int getSize() {
        return 45;
    }
}
