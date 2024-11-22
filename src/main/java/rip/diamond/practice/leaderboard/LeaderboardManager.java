package rip.diamond.practice.leaderboard;

import lombok.Getter;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.leaderboard.impl.KitLeaderboard;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.Tasks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class LeaderboardManager {
    private final Map<Kit, KitLeaderboard> winsLeaderboard = new HashMap<>();
    private final Map<Kit, KitLeaderboard> eloLeaderboard = new HashMap<>();
    private final Map<Kit, KitLeaderboard> winstreakLeaderboard = new HashMap<>();
    private final Map<Kit, KitLeaderboard> bestWinstreakLeaderboard = new HashMap<>();

    public void init() {
        if (!Config.MONGO_ENABLED.toBoolean()) {
            return;
        }
        for (Kit kit : Kit.getKits()) {
            winsLeaderboard.put(kit, new KitLeaderboard(LeaderboardType.WINS, kit));
            eloLeaderboard.put(kit, new KitLeaderboard(LeaderboardType.ELO, kit));
            winstreakLeaderboard.put(kit, new KitLeaderboard(LeaderboardType.WINSTREAK, kit));
            bestWinstreakLeaderboard.put(kit, new KitLeaderboard(LeaderboardType.BEST_WINSTREAK, kit));
        }

        Tasks.runAsyncTimer(this::update, 0L, 20L * 60L * 60L); //Updates each 60 minutes
    }

    public void update() {
        long previous = System.currentTimeMillis();
        Common.debug("Updating the leaderboard... this may take a while");
        for (Map<Kit, KitLeaderboard> datas : Arrays.asList(winsLeaderboard, eloLeaderboard, winstreakLeaderboard, bestWinstreakLeaderboard)) {
            //Ranking updated every five minutes
            datas.values().forEach(Leaderboard::update);
        }
        long current = System.currentTimeMillis();
        Common.debug("Ranking updated! Time consuming " + (current - previous) + "ms");
    }

}
