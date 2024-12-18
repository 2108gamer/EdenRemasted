package rip.diamond.practice.rank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Rank {

    String getName(UUID uuid);
    String getPrefix(UUID uuid);
    String getSuffix(UUID uuid);
    String getColor(UUID uuid);

    ChatColor getChatColor(UUID uuid);

    String getTag(Player player);
    String getRealName(Player player);
    int getWeight(UUID uuid);
}
