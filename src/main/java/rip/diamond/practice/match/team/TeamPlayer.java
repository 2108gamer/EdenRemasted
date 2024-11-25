package rip.diamond.practice.match.team;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.kits.KitLoadout;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.Tasks;
import rip.diamond.practice.util.TitleSender;
import rip.diamond.practice.util.Util;

import java.util.UUID;

@Getter
public class TeamPlayer {

	private final UUID uuid;
	private final String username;
	@Setter private boolean alive = true;
	@Setter private boolean respawning = false;
	@Setter private boolean disconnected = false;
	@Setter private int potionsThrown;
	@Setter private int potionsMissed;
	@Setter private int hits;
	@Setter private int blockedHits;
	@Setter private int gotHits;
	@Setter private TeamPlayer lastHitDamager;
	@Setter private int combo;
	@Setter private int longestCombo;
	@Setter private double damageDealt;
	@Setter private KitLoadout kitLoadout;
	@Setter private long protectionUntil = -1;

	public TeamPlayer(Player player) {
		this.uuid = player.getUniqueId();
		this.username = player.getName();
	}

	public TeamPlayer(UUID uuid, String username) {
		this.uuid = uuid;
		this.username = username;
	}

	public Player getPlayer() {
		if (Util.isNPC(uuid)) {
			return Eden.INSTANCE.getHookManager().getCitizensHook().getNPCPlayer(uuid);
		}
		return Bukkit.getPlayer(uuid);
	}

	public PlayerProfile getPlayerProfile() {
		return PlayerProfile.get(uuid);
	}

	public int getPing() {
		Player player = getPlayer();
		return player == null ? 0 : player.spigot().getPing();
	}

	public void broadcastTitle(String title, String subtitle) {
		TitleSender.sendTitle(getPlayer(), title, PacketPlayOutTitle.EnumTitleAction.TITLE, 0, Config.MATCH_END_DURATION.toInteger() ,5);
		TitleSender.sendTitle(getPlayer(), subtitle, PacketPlayOutTitle.EnumTitleAction.SUBTITLE, 0, Config.MATCH_END_DURATION.toInteger() ,5);
	}

	public void teleport(Location location) {
		if (getPlayer() == null) {
			return;
		}
		Tasks.run(()-> Util.teleport(getPlayer(), location));
	}

	public void addPotionsThrown() {
		potionsThrown++;
	}

	public void addPotionsMissed() {
		potionsMissed++;
	}

	public void handleHit(double damage) {
		hits++;
		combo++;
		damageDealt = damageDealt + damage;
		if (combo > longestCombo) {
			longestCombo = combo;
		}
		protectionUntil = -1;
	}

	public void handleGotHit(TeamPlayer damager, boolean blockedHit) {
		gotHits++;
		combo = 0;
		lastHitDamager = damager;
		if (blockedHit) {
			blockedHits++;
		}
	}

	public void respawn(Match match) {
		if (getPlayer() == null) {
			return;
		}
		kitLoadout.apply(match, getPlayer());
		((CraftPlayer) getPlayer()).getHandle().setAbsorptionHearts(0);
		getPlayer().setHealth(getPlayer().getMaxHealth()); //A fix for #11 - Restore health each time when respawn
		getPlayer().getActivePotionEffects().clear(); //A fix for #389 - Remove effects like absorption when score
		lastHitDamager = null; //A fix for #11 - Prevent kill spam (https://www.youtube.com/watch?v=oD6k0rrNVTk)
	}

}
