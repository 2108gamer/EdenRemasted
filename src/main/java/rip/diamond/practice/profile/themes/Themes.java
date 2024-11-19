package rip.diamond.practice.profile.themes;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.Common;

@Getter
public enum Themes {
    ORANGE("Orange", ChatColor.GOLD),
    GREEN("Green", ChatColor.GREEN),
    AQUA("Aqua", ChatColor.AQUA),
    RED("Red", ChatColor.RED),
    YELLOW("Yellow", ChatColor.YELLOW),
    PURPLE("PURPLE", ChatColor.DARK_PURPLE),
    PINK("Pink", ChatColor.LIGHT_PURPLE),
    BLUE("BLUE", ChatColor.BLUE),
    WHITE("WHITE", ChatColor.WHITE);

    private final String name;
    private final ChatColor color;

    Themes(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public static void applyTheme(Player player, Themes theme) {
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile == null) return;

        profile.setTheme(theme);
        profile.save(false, result -> {
            if (result) {
                Common.debug(player + "'s theme saved successfully");
            } else {
                Common.debug("Hubo un error al guardar el tema de: " + player + ".");
            }
        });

    }
}