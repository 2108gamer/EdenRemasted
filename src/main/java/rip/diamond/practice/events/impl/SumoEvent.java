package rip.diamond.practice.events.impl;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;
import rip.diamond.practice.EdenPlaceholder;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.event.MatchPlayerDeathEvent;
import rip.diamond.practice.event.MatchRoundStartEvent;
import rip.diamond.practice.event.PartyDisbandEvent;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.events.EventCountdown;
import rip.diamond.practice.events.EventState;
import rip.diamond.practice.events.EventType;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.impl.SumoEventMatch;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.party.PartyMember;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Getter
public class SumoEvent extends EdenEvent {

    private SumoEventState sumoEventState = SumoEventState.NONE;
    private SumoEventMatch match;
    private int round = 0;
    private Team teamA, teamB;

    public SumoEvent(String hoster, int minPlayers, int maxPlayers, int teamSize) {
        super(hoster, EventType.SUMO_EVENT, minPlayers, maxPlayers, teamSize);
    }

    private boolean canEnd() {
        return getState() == EventState.RUNNING && getParties().size() <= 1;
    }

    public boolean isFighting(Team team) {
        return teamA == team || teamB == team;
    }

    @Override
    public Listener constructListener() {
        return new Listener() {
            //Use this event as detecting when the match start countdown is ended
            @EventHandler
            public void onStart(MatchRoundStartEvent event) {
                if (event.getMatch() == match) {
                    startNewRound();
                }
            }

            @EventHandler
            public void onMove(PlayerMoveEvent event) {
                Player player = event.getPlayer();
                PlayerProfile profile = PlayerProfile.get(player);

                if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null && profile.getMatch() instanceof SumoEventMatch) {
                    Match match = profile.getMatch();
                    Team team = match.getTeam(player);

                    if (isFighting(team) && sumoEventState == SumoEventState.STARTING_NEW_ROUND) {
                        Util.teleport(player, event.getFrom());
                    }
                }
            }

            @EventHandler
            public void onDamage(EntityDamageEvent event) {
                if (!(event.getEntity() instanceof Player)) {
                    return;
                }
                Player player = (Player) event.getEntity();
                PlayerProfile profile = PlayerProfile.get(player);

                if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null && profile.getMatch() instanceof SumoEventMatch) {
                    Match match = profile.getMatch();
                    Team team = match.getTeam(player);
                    TeamPlayer teamPlayer = match.getTeamPlayer(player);

                    if (!isFighting(team)) {
                        event.setCancelled(true);
                        return;
                    }

                    event.setDamage(0);
                }
            }

            @EventHandler
            public void onDeath(MatchPlayerDeathEvent event) {
                Player player = event.getPlayer();
                PlayerProfile profile = PlayerProfile.get(player);

                if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null && profile.getMatch() instanceof SumoEventMatch) {
                    Match match = profile.getMatch();
                    Team team = match.getTeam(player);
                    TeamPlayer teamPlayer = match.getTeamPlayer(player);

                    if (team.isEliminated()) {
                        broadcast(Language.EVENT_SUMO_EVENT_MATCH_END_MESSAGE.toString(match.getOpponentTeam(team).getLeader().getUsername(), team.getLeader().getUsername()));

                        Party party = Party.getByPlayer(team.getLeader().getPlayer());
                        //Party may be null if the player exits the server during combat
                        if (party != null) {
                            eliminate(party);
                        }

                        if (canEnd()) {
                            end(false);
                        }
                    }
                }
            }

            @EventHandler
            public void onDisband(PartyDisbandEvent event) {
                if (canEnd()) {
                    end(false);
                }
            }
        };
    }

    @Override
    public List<String> getLobbyScoreboard(Player player) {
        /*
         * If sumoEventState == SumoEventState.NONE, it means the sumo wrestling match has not started yet
         * In this case, getState() should return EventState.WAITING
         */
        if (sumoEventState == SumoEventState.NONE) {
            return Language.EVENT_SUMO_EVENT_LOBBY_SCOREBOARD_STARTING_EVENT.toStringList(player);
        }
        /*
         * If sumoEventState == SumoEventState.STARTING_NEW_ROUND or SumoEventState.FIGHTING, it means that the sumo wrestling round has started and the players in the event are fighting.
         * In this case, getState() should return EventState.RUNNING
         */
        if (sumoEventState == SumoEventState.STARTING_NEW_ROUND || sumoEventState == SumoEventState.FIGHTING) {
            return Language.EVENT_SUMO_EVENT_LOBBY_SCOREBOARD_FIGHTING.toStringList(player, round, teamA.getLeader().getUsername(), teamB.getLeader().getUsername());
        }
        return ImmutableList.of(EdenPlaceholder.SKIP_LINE);
    }

    @Override
    public List<String> getInGameScoreboard(Player player) {
        if (state == EventState.RUNNING) {
            /*
             * If sumoEventState == SumoEventState.NONE, it means the sumo wrestling match has not started yet
             * In this case, getState() should return EventState.WAITING
             */
            if (sumoEventState == SumoEventState.NONE) {
                return Language.EVENT_SUMO_EVENT_IN_GAME_SCOREBOARD_STARTING_MATCH.toStringList(player);
            }
            /*
             * If sumoEventState == SumoEventState.STARTING_NEW_ROUND or SumoEventState.FIGHTING, it means that the sumo wrestling round has started and the players in the event are fighting.
             * In this case, getState() should return EventState.RUNNING
             *
             * This is special, because at the end of each round of combat, sumoEventState will be SumoEventState.ENDING, which does not mean that the entire activity has ended, so when sumoEventState == SumoEventState.ENDING we can also display the score board of the ongoing battle.
             */
            if (sumoEventState == SumoEventState.STARTING_NEW_ROUND || sumoEventState == SumoEventState.FIGHTING || sumoEventState == SumoEventState.ENDING) {
                return Language.EVENT_SUMO_EVENT_IN_GAME_SCOREBOARD_FIGHTING.toStringList(player, getTeamName(teamA), getTeamName(teamB));
            }
        }

        //Here is what will be displayed when the entire activity has ended.
        if (state == EventState.ENDING) {
            return Language.EVENT_SUMO_EVENT_IN_GAME_SCOREBOARD_ENDING.toStringList(player);
        }
        return null;
    }

    @Override
    public List<String> getStatus(Player player) {
        /*
         * If tournamentState == TournamentState.NONE, it means the tournament has not started yet
         * In this case, getState() should return EventState.WAITING
         */
        if (sumoEventState == SumoEventState.NONE) {
            return Language.EVENT_TOURNAMENT_STATUS_STARTING_EVENT.toStringList(player, getUncoloredEventName());
        }
        /*
         * If tournamentState == TournamentState.STARTING_NEW_ROUND, it means the tournament is preparing to start a new round
         * In this case, getState() should return EventState.RUNNING
         */
        else if (sumoEventState == SumoEventState.STARTING_NEW_ROUND) {
            return Language.EVENT_TOURNAMENT_STATUS_STARTING_NEW_ROUND.toStringList(player, getUncoloredEventName(), round);
        }
        /*
         * If tournamentState == TournamentState.FIGHTING, it means that the tournament round has started and the players in the event are fighting.
         * In this case, getState() should return EventState.RUNNING
         */
        else if (sumoEventState == SumoEventState.FIGHTING) {
            return Language.EVENT_TOURNAMENT_STATUS_FIGHTING.toStringList(player, getUncoloredEventName(), round, getTeamName(teamA), getTeamName(teamB));
        } else return null;
    }

    @Override
    public void start() {
        super.start();

        String kitName = Config.EVENT_SUMO_EVENT_KIT.toString();
        Kit kit = Kit.getByName(kitName);
        if (kit == null) {
            broadcastToEventPlayers("&c[Practice] Unable to find a kit named " + kitName + ", please contact an administrator.");
            end(true);
            return;
        }
        List<String> arenaNames = Config.EVENT_SUMO_EVENT_ARENAS.toStringList();
        Arena arena = Arena.getArena(arenaNames.get(new Random().nextInt(arenaNames.size())));
        ArenaDetail arenaDetail = Arena.getArenaDetail(arena);
        if (arenaDetail == null) {
            broadcastToEventPlayers("&c[Practice] Unable to find a usable arena, please contact an administrator.");
            end(true);
            return;
        }
        List<Team> teams = new ArrayList<>();
        parties.forEach(party -> party.getAllPartyMembers().forEach(partyMember -> teams.add(new Team(new TeamPlayer(partyMember.getPlayer())))));
        match = new SumoEventMatch(this, arenaDetail, kit, teams);
        match.start();

        if (canEnd()) {
            end(false);
        }
    }

    @Override
    public void end(boolean forced) {
        super.end(forced);
        sumoEventState = SumoEventState.ENDING;

        if (forced) {
            broadcast(Language.EVENT_FORCE_CANCEL_EVENT.toString(getEventType().getName()));
            //This line of code has to be run in the last. This is to unregister the events
            destroy();
            return;
        }

        if (getParties().isEmpty()) {
            broadcast(Language.EVENT_NO_WINNER_BECAUSE_NO_PARTY.toString());
            //This line of code has to be run in the last. This is to unregister the events
            destroy();
            return;
        }

        new BukkitRunnable() {
            private final String winners = getParties().get(0).getAllPartyMembers().stream().map(PartyMember::getUsername).collect(Collectors.joining(Language.EVENT_WINNER_ANNOUNCE_SPLIT_FORMAT.toString()));
            private int count = 5;
            @Override
            public void run() {
                if (count == 0) {
                    cancel();
                    //This line of code has to be run in the last. This is to unregister the events
                    destroy();
                } else {
                    broadcast(Language.EVENT_WINNER_ANNOUNCE_MESSAGE.toString(winners));
                    count--;
                }
            }
        }.runTaskTimer(Eden.INSTANCE, 0L, 20L);
    }

    private void startNewRound() {
        round++;

        List<Team> teams = match.getTeams().stream().filter(team -> !team.isEliminated()).collect(Collectors.toList());
        Collections.shuffle(teams);
        teamA = teams.get(0);
        teamB = teams.get(1);

        teamA.teleport(match.getArenaDetail().getA());
        teamB.teleport(match.getArenaDetail().getB());

        //We need to set the teamA and teamB first before setting the SumoEventState. This is to prevent scoreboard throw an NPE error.
        sumoEventState = SumoEventState.STARTING_NEW_ROUND;

        setCountdown(new EventCountdown(false,5, 5,4,3,2,1) {
            @Override
            public void runUnexpired(int tick) {
                broadcastToEventPlayers(Language.EVENT_SUMO_EVENT_NEW_ROUND_COUNTDOWN.toString(round, tick));
            }
            @Override
            public void runExpired() {
                sumoEventState = SumoEventState.FIGHTING;
                broadcastToEventPlayers(Language.EVENT_SUMO_EVENT_NEW_ROUND_STARTED.toString());
            }
        });
    }

    @Override
    public void eliminate(Party party) {
        super.eliminate(party);
        sumoEventState = SumoEventState.ENDING;

        party.teleport(match.getArenaDetail().getSpectator());

        setCountdown(new EventCountdown(true, 3) {
            @Override
            public void runUnexpired(int tick) {

            }

            @Override
            public void runExpired() {
                if (canEnd()) {
                    end(false);
                    return;
                }
                startNewRound();
            }
        });
    }

    enum SumoEventState {
        NONE,
        STARTING_NEW_ROUND,
        FIGHTING,
        ENDING
    }
}
