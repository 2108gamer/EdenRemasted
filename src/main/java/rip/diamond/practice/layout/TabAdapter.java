package rip.diamond.practice.layout;

import io.github.nosequel.tab.shared.entry.TabElement;
import io.github.nosequel.tab.shared.entry.TabElementHandler;
import io.github.nosequel.tab.shared.skin.SkinUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.util.tablist.util.Skin;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class TabAdapter implements TabElementHandler {

    @Override
    public TabElement getElement(Player player) {
        final TabElement element = new TabElement();

        element.setHeader(getHeader(player));
        element.setFooter(getFooter(player));

        List<Player> onlinePlayers = (List<Player>) Bukkit.getOnlinePlayers();
        int slot = 0;

        for (Player onlinePlayer : onlinePlayers) {
            try {

                Skin skin = Skin.fromPlayer(onlinePlayer);
                String skullTexture = skin != null ? skin.skinValue : "";


                if (!skullTexture.isEmpty()) {
                    element.add(slot, 0, skullTexture);
                    element.add(slot, 1, ChatColor.GREEN + onlinePlayer.getName());
                } else {
                    element.add(slot, 0, "");
                    element.add(slot, 1, ChatColor.RED + onlinePlayer.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
                element.add(slot, 0, "");
                element.add(slot, 1, ChatColor.RED + "Error: " + onlinePlayer.getName());
            }

            slot++;
        }

        return element;
    }



    public String getHeader(Player player) {
        Language.TABLIST_HEADER.toString();
        return Language.TABLIST_HEADER.toString(player);
    }


    public String getFooter(Player player) {
        Language.TABLIST_FOOTER.toString();
        return Language.TABLIST_FOOTER.toString(player);
    }
    private boolean isBase64Encoded(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
