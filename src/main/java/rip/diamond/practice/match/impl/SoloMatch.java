package rip.diamond.practice.match.impl;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchState;
import rip.diamond.practice.match.MatchType;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.profile.data.ProfileKitData;
import rip.diamond.practice.profile.divisions.Division;
import rip.diamond.practice.profile.divisions.DivisionManager;
import rip.diamond.practice.queue.QueueType;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Clickable;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class SoloMatch extends Match {

    private final Team teamA;
    private final Team teamB;

    private final TeamPlayer playerA;
    private final TeamPlayer playerB;

    public SoloMatch(ArenaDetail arenaDetail, Kit kit, Team teamA, Team teamB, QueueType queueType, boolean duel) {
        super(arenaDetail, kit, Arrays.asList(teamA, teamB));

        this.teamA = teamA;
        this.teamB = teamB;

        this.playerA = teamA.getLeader();
        this.playerB = teamB.getLeader();

        setQueueType(queueType);
        setDuel(duel);

        Common.debug("Starting a SoloMatch battle(" + playerA.getUsername() + " vs " + playerB.getUsername() + ") (Kit: " + kit.getName() + ") (Map: " + arenaDetail.getArena().getName() + ") (UUID: " + getUuid() + ") (Duel: " + duel + ")");
    }

    @Override
    public void setupTeamSpawnLocation() {
        teamA.setSpawnLocation(getArenaDetail().getA());
        teamB.setSpawnLocation(getArenaDetail().getB());
    }

    @Override
    public void displayMatchEndMessages() {
        TeamPlayer tWinner = getWinningPlayers().get(0);
        TeamPlayer tLoser = getOpponent(getWinningPlayers().get(0));

        Clickable clickable = new Clickable(Language.MATCH_POST_MATCH_INVENTORY_WINNER + tWinner.getUsername(), Language.MATCH_POST_MATCH_INVENTORY_HOVER.toString(tWinner.getUsername()), "/viewinv " + tWinner.getUuid());
        clickable.add(CC.GRAY + " - ");
        clickable.add(Language.MATCH_POST_MATCH_INVENTORY_LOSER + tLoser.getUsername(), Language.MATCH_POST_MATCH_INVENTORY_HOVER.toString(tLoser.getUsername()), "/viewinv " + tLoser.getUuid());

        Language.MATCH_POST_MATCH_INVENTORY_MESSAGE.toStringList().forEach(s -> {
            if (s.contains("{post-match-inventories}")) {
                getPlayersAndSpectators().forEach(clickable::sendToPlayer);
            } else {
                getPlayersAndSpectators().forEach(p -> Common.sendMessage(p, s));
            }
        });
    }

    @Override
    public void displayMatchEndTitle() {


        TeamPlayer tWinner = getWinningPlayers().get(0);
        TeamPlayer tLoser = getOpponent(getWinningPlayers().get(0));
        PlayerProfile profile = PlayerProfile.get(tWinner.getPlayer());
        ChatColor def = CC.getColorFromName(Config.DEFAULT_THEME.toString());
        Object theme = profile.getSettings().get(ProfileSettings.THEME_SELECTION);
        if (theme == null) {
            theme = Config.DEFAULT_THEME;
        }
        ChatColor c = CC.getColorFromName(theme.toString());
        if (c == null) {
            c = def;
        }
        PlayerProfile profi = PlayerProfile.get(tLoser.getPlayer());
        ChatColor de = CC.getColorFromName(Config.DEFAULT_THEME.toString());
        Object them = profile.getSettings().get(ProfileSettings.THEME_SELECTION);
        if (them == null) {
            them = Config.DEFAULT_THEME;
        }
        ChatColor co = CC.getColorFromName(them.toString());
        if (co == null) {
            co = de;
        }

        assert c != null;
        tWinner.broadcastTitle(Language.MATCH_END_TITLE_WIN_TITLE.toString(), Language.MATCH_END_TITLE_WIN_SUBTITLE.toString(tWinner.getUsername()).replace("<theme>", c.toString()));
        assert co != null;
        tLoser.broadcastTitle(Language.MATCH_END_TITLE_LOSE_TITLE.toString(), Language.MATCH_END_TITLE_LOSE_SUBTITLE.toString(tWinner.getUsername()).replace("<theme>", co.toString()));
    }

    @Override
    public void calculateMatchStats() {
        TeamPlayer tWinner = getWinningPlayers().get(0);
        TeamPlayer tLoser = getOpponent(getWinningPlayers().get(0));

        //Set Post-Match Inventories swtich to
        getPostMatchInventories().get(tWinner.getUuid()).setSwitchTo(tLoser.getUsername(), tLoser.getUuid());
        getPostMatchInventories().get(tLoser.getUuid()).setSwitchTo(tWinner.getUsername(), tWinner.getUuid());

        //Because this is a duel match, we don't increase win/lose in player statistics and don't calculate the elo afterwards
        if (isDuel()) {
            return;
        }

        PlayerProfile pWinner = PlayerProfile.get(tWinner.getUuid());
        PlayerProfile pLoser = PlayerProfile.get(tLoser.getUuid());

        ProfileKitData kWinner = pWinner.getKitData().get(getKit().getName());
        ProfileKitData kLoser = pLoser.getKitData().get(getKit().getName());

        if (getQueueType() == QueueType.RANKED) {
            int oldWinnerElo = kWinner.getElo();
            int oldLoserElo = kLoser.getElo();

            int newWinnerElo = Util.getNewRating(oldWinnerElo, oldLoserElo, 1);
            int newLoserElo = Util.getNewRating(oldLoserElo, oldWinnerElo, 0);

            kWinner.setElo(newWinnerElo);
            kLoser.setElo(newLoserElo);

            int winnerEloChange = newWinnerElo - oldWinnerElo;
            int loserEloChange = oldLoserElo - newLoserElo;

            broadcastMessage(Language.MATCH_POST_MATCH_INVENTORY_RATING_CHANGES.toString(pWinner.getUsername(), winnerEloChange, newWinnerElo, pLoser.getUsername(), loserEloChange, newLoserElo));
        }

        kWinner.incrementWon(getQueueType() == QueueType.RANKED);
        kLoser.incrementLost(getQueueType() == QueueType.RANKED);
        int totalKitWinsWinner = kWinner.getWon();
        Division newDivisionWinner = DivisionManager.getDivisionByWins(totalKitWinsWinner, DivisionManager.getDivisions());




            kWinner.setDivision(newDivisionWinner.getDisplayName());



            Division nextDivision = DivisionManager.getNextDivision(newDivisionWinner);

            if (nextDivision != null) {
                int winsToNextDivision = nextDivision.getWinsMin() - kWinner.getWon();
               String progress = DivisionManager.getProgressBar(totalKitWinsWinner, newDivisionWinner);


                if (winsToNextDivision > 0) {
                    Language.UPDATE_DIVISION.sendListOfMessage(pWinner.getPlayer(), newDivisionWinner.getDisplayName(), winsToNextDivision, nextDivision.getDisplayName(), progress, kWinner.getBestWinstreak());
                }
            } else {

                tWinner.getPlayer().sendMessage("§a¡Has alcanzado la división más alta!");
            }


            pWinner.save(true, sucess -> {
                if(sucess) {
                Common.debug("saved division data for player" + pWinner.getUsername() + "Division" + newDivisionWinner);
                }
            });




        kWinner.calculateWinstreak(true);
        kLoser.calculateWinstreak(false);

        kWinner.getWon();

        List<String> winCommands = Config.MATCH_WIN_COMMANDS.toStringList();
        List<String> loseCommands = Config.MATCH_LOSE_COMMANDS.toStringList();
        if (!winCommands.isEmpty()) {
            for (String cmd : winCommands) {
                String c = cmd.replace("{player}", tWinner.getUsername());
                Common.debug("Executing background instructions " + c);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
            }
        }
        if (!loseCommands.isEmpty()) {
            for (String cmd1 : loseCommands) {
                String d = cmd1.replace("{loser-player}", tLoser.getUsername());
                Common.debug("Executing background instructions " + d);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), d);
            }
        }
    }

    @Override
    public MatchType getMatchType() {
        return MatchType.SOLO;
    }

    @Override
    public List<TeamPlayer> getWinningPlayers() {
        if (getState() != MatchState.ENDING) {
            throw new PracticeUnexpectedException("Cannot get Winning Players when match isn't ending");
        }
        if (playerA.isDisconnected() || !playerA.isAlive()) {
            return Collections.singletonList(playerB);
        } else {
            return Collections.singletonList(playerA);
        }
    }

    @Override
    public Team getWinningTeam() {
        if (teamA.isEliminated()) {
            return teamB;
        } else if (teamB.isEliminated()) {
            return teamA;
        } else {
            return null;
        }
    }

    @Override
    public List<String> getMatchScoreboard(Player player) {
        List<String> elements = new ArrayList<>();

        if (getState() == MatchState.ENDING) {
            elements.addAll(Language.SCOREBOARD_IN_MATCH_SOLO_ENDING.toStringList(player));
        } else {
            if (getKit().getGameRules().isBoxing()) {
                elements.addAll(Language.SCOREBOARD_IN_MATCH_SOLO_BOXING.toStringList(player));
            } else if (getKit().getGameRules().isBed()) {
                elements.addAll(Language.SCOREBOARD_IN_MATCH_SOLO_BED.toStringList(player));
            } else if (getKit().getGameRules().isPoint(this)) {
                elements.addAll(Language.SCOREBOARD_IN_MATCH_SOLO_POINT.toStringList(player));
            } else {
                elements.addAll(Language.SCOREBOARD_IN_MATCH_SOLO_FIGHTING.toStringList(player));
            }
        }
        return elements;
    }

    @Override
    public List<String> getSpectateScoreboard(Player player) {
        List<String> elements = new ArrayList<>();
        TeamPlayer playerA = getPlayerA();
        TeamPlayer playerB = getPlayerB();
        
        if (getState() == MatchState.ENDING) {
            elements.addAll(Language.SCOREBOARD_IN_SPECTATE_SOLO_ENDING.toStringList(player));
        } else {
            if (getKit().getGameRules().isBoxing()) {
                elements.addAll(Language.SCOREBOARD_IN_SPECTATE_SOLO_BOXING.toStringList(player));
            } else if (getKit().getGameRules().isBed()) {
                elements.addAll(Language.SCOREBOARD_IN_SPECTATE_SOLO_BED.toStringList(player));
            } else if (getKit().getGameRules().isPoint(this)) {
                elements.addAll(Language.SCOREBOARD_IN_SPECTATE_SOLO_POINT.toStringList(player));
            } else {
                elements.addAll(Language.SCOREBOARD_IN_SPECTATE_SOLO_FIGHTING.toStringList(player));
            }
        }
        
        return elements;
    }

    @Override
    public Team getOpponentTeam(Team team) {
        if (teamA.equals(team)) {
            return teamB;
        } else if (teamB.equals(team)) {
            return teamA;
        } else {
            return null;
        }
    }

    @Override
    public TeamPlayer getOpponent(TeamPlayer teamPlayer) {
        if (teamPlayer == null) {
            return null;
        }

        if (playerA == teamPlayer) {
            return playerB;
        } else if (playerB == teamPlayer) {
            return playerA;
        } else {
            throw new PracticeUnexpectedException("Cannot get opponent player from a solo match.");
        }
    }




}
