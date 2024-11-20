package rip.diamond.practice.lobby;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Eden;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.*;
import rip.diamond.practice.util.serialization.LocationSerialization;

@Getter
public class LobbyManager {

    private final Eden plugin;

    @Setter private Location spawnLocation = null;

    public LobbyManager(Eden plugin) {
        this.plugin = plugin;
        try {
            this.spawnLocation = LocationSerialization.deserializeLocation(plugin.getLocationFile().getString("spawn-location"));
        } catch (Exception e) {
            Common.log("Unable to deserialize spawn-location from location file.");
        }
    }

    public void teleport(Player player) {
        if (spawnLocation == null) {
            Common.sendMessage(player, CC.RED + "Unable to teleport you to a certain location. Please check if spawn location and editor location is setup correctly.");
            Common.log(CC.RED + "Unable to teleport " + player.getName() + " to a certain location. Please check if spawn location and editor location is setup correctly.");
            return;
        }
        Util.teleport(player, spawnLocation);
    }

    public void reset(Player player) {
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile == null) {
            return;
        }

        PlayerUtil.reset(player);

        profile.setMatch(null);
        profile.setPlayerState(PlayerState.IN_LOBBY);
        profile.setupItems();
        profile.getCooldowns().forEach((name, cooldown) -> cooldown.cancelCountdown());

        if (player.hasPermission("eden.extra.lobby-flight")) {
            player.setAllowFlight(true);
        }
    }
    public void resetKit(Player player) {
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile == null) {
            return;
        }

        PlayerUtil.res(player, false);

        profile.setupItems();

        profile.setMatch(null);
        profile.setPlayerState(PlayerState.IN_LOBBY);
        profile.setupItems();
    }




    public void sendToSpawnAndReset(Player player) {
        Tasks.run(()-> {
            reset(player);
            teleport(player);
        });
    }
    public void EditorEvent(Player player) {
        Tasks.run(()-> {
            resetKit(player);
        });
    }
}