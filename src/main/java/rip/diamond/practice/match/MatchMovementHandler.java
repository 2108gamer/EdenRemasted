package rip.diamond.practice.match;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import rip.diamond.practice.Eden;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitGameRules;
import rip.diamond.practice.match.task.MatchClearBlockTask;
import rip.diamond.practice.match.task.MatchRespawnTask;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.profile.cooldown.CooldownType;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.cuboid.CuboidDirection;

import java.util.Comparator;
import java.util.List;

public class MatchMovementHandler {

    public MatchMovementHandler() {
        Eden.INSTANCE.getSpigotAPI().getMovementHandler().injectLocationUpdate((player, from, to) -> {
            PlayerProfile profile = PlayerProfile.get(player);

            Block block = to.getBlock();
            Block underBlock = to.clone().add(0, -1, 0).getBlock();

            if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
                Match match = profile.getMatch();
                ArenaDetail arenaDetail = match.getArenaDetail();
                Arena arena = arenaDetail.getArena();
                Kit kit = match.getKit();
                KitGameRules gameRules = kit.getGameRules();

                if (gameRules.isStartFreeze() && match.getState() == MatchState.STARTING && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
                    Location location = match.getTeam(player).getSpawnLocation();
                    //https://github.com/diamond-rip/Eden/issues/389#issuecomment-1630048579 - Smoother looking by only changing the player's x and z location
                    location.setY(from.getY());
                    location.setPitch(from.getPitch());
                    location.setYaw(from.getYaw());
                    Util.teleport(player, location);
                    return;
                }

                if ((!arenaDetail.getCuboid().clone().outset(CuboidDirection.HORIZONTAL, 10).contains(player) && Config.MATCH_OUTSIDE_CUBOID_INSTANT_DEATH.toBoolean()) || arena.getYLimit() > player.getLocation().getY()) {

                   /* Util.damage(player, 99999); */
                    onDeath(player);
                    return;
                }

                //Prevent any duplicate scoring
                //If two people go into the portal at the same time in bridge, it will count as +2 points
                //If player go into the water and PlayerMoveEvent is too slow to perform teleportation, it will run MatchNewRoundTask multiple times
                if (match.getMatchPlayers().stream().allMatch(p -> PlayerProfile
                        .get(p)
                        .getCooldowns()
                        .get(CooldownType.SCORE)
                        .isExpired())) {
                    TeamPlayer teamPlayer = match.getTeamPlayer(player);
                    if (match.getState() == MatchState.FIGHTING && !teamPlayer.isRespawning()) {
                        //Check KitGameRules for Death on Water
                        if (gameRules.isDeathOnWater() && (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER)) {
                            if (gameRules.isPoint(match)) {
                                TeamPlayer lastHitDamager = teamPlayer.getLastHitDamager();
                                //Players have a chance to die without being attacked by the enemy, such as lava. If so, just randomly draw a player from the enemy team.
                                if (lastHitDamager == null) {
                                    lastHitDamager = match.getOpponentTeam(match.getTeam(player)).getAliveTeamPlayers().get(0);
                                }
                                match.score(profile, teamPlayer, lastHitDamager);
                            } else {

                                Util.damage(player, 99999);
                            }
                            return;
                        }

                        //Check KitGameRules into target
                        if (gameRules.isPortalGoal() && block.getType() == Material.ENDER_PORTAL) {
                            Team playerTeam = match.getTeam(player);
                            Team portalBelongsTo = match.getTeams().stream().min(Comparator.comparing(team -> team.getSpawnLocation().distance(to))).orElse(null);
                            if (portalBelongsTo == null) {
                                Common.log("An error occurred while finding portalBelongsTo, please contact GoodestEnglish to fix");
                                return;
                            }
                            if (portalBelongsTo != playerTeam) {
                                match.score(profile, null, match.getTeamPlayer(player));
                            } else {
                                //Prevent player scoring their own goal
                                Common.debug("Pase por el evento en la liinea 92 de match movement handler");
                                Util.damage(player, 99999);

                            }
                        }
                    }
                }
            } else if (profile.getPlayerState() == PlayerState.IN_SPECTATING && profile.getMatch() != null) {
                Match match = profile.getMatch();
                ArenaDetail arenaDetail = match.getArenaDetail();
                Arena arena = arenaDetail.getArena();

                if (!arenaDetail.getCuboid().clone().outset(CuboidDirection.HORIZONTAL, Config.MATCH_SPECTATE_EXPEND_CUBOID.toInteger()).contains(player) || arena.getYLimit() > player.getLocation().getY()) {
                    player.teleport(arenaDetail.getSpectator());
                }
            }
        });
    }

    public void onDeath(Player player) {


        PlayerProfile profile = PlayerProfile.get(player);

        if(profile.getMatch().canEnd()) {
            return;
        }

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();
            TeamPlayer teamPlayer = match.getTeamPlayer(player);



          Common.debug("player" + player.getDisplayName().toLowerCase());
            KitGameRules gameRules = match.getKit().getGameRules();
            /* Common.debug("Sonidos de muerte reproducidos"); */


           if(profile.getParty() == null) {
               Player opponent = match.getOpponent(match.getTeamPlayer(player)).getPlayer();
               if(opponent != player) {
                   opponent.playSound(opponent.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);

               } else {
                   player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);

               }
           }







            if ((gameRules.isBed() && !match.getTeam(player).isBedDestroyed()) || gameRules.isBreakGoal() || gameRules.isPortalGoal()) {

                new MatchRespawnTask(match, teamPlayer);
            } else if (gameRules.isPoint(match)) {
                TeamPlayer lastHitDamager = teamPlayer.getLastHitDamager();
                //Players have a chance to die without being attacked by the enemy, such as lava. If so, just randomly draw a player from the enemy team.
                if (lastHitDamager == null) {
                    lastHitDamager = match.getOpponentTeam(match.getTeam(player)).getAliveTeamPlayers().get(0);
                }
                match.score(profile, teamPlayer, lastHitDamager);
            } else {
                match.die(player, false);
            }



            if (gameRules.isClearBlock()) {
                match.getTasks().stream()
                        .filter(task -> task instanceof MatchClearBlockTask)
                        .map(task -> (MatchClearBlockTask) task)
                        .filter(task -> task.getBlockPlacer() == teamPlayer)
                        .forEach(task -> task.setActivateCallback(false));
            }
        }

        player.setHealth(20);
        player.setVelocity(new Vector());



        if (Config.MATCH_TP_2_BLOCKS_UP_WHEN_DIE.toBoolean()) {
            Util.teleport(player, player.getLocation().clone().add(0,2,0)); //Teleport 2 blocks higher, to try to re-do what MineHQ did (Make sure to place this line of code after setHealth, otherwise it won't work)
        }
    }
}
