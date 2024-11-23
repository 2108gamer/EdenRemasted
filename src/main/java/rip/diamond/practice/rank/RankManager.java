package rip.diamond.practice.rank;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import rip.diamond.practice.rank.impl.AquaCore;
import rip.diamond.practice.util.Common;

@Getter @Setter
public class RankManager {

    @Getter
    private static RankManager instance;
    private Plugin plugin;
    private String rankSystem;
    private Rank rank;

    public RankManager(Plugin plugin) {
        instance = this;
        this.plugin = plugin;
    }

    public void loadRank() {

        if (Bukkit.getPluginManager().getPlugin("AquaCore") != null) {
            this.setRank(new AquaCore());
            this.setRankSystem("AquaCore");
            Common.debug("enabled support for AquaCore");
        }
    }
}
