package rip.diamond.practice.layout;

import io.github.epicgo.sconey.element.SconeyElement;
import io.github.epicgo.sconey.element.SconeyElementAdapter;
import io.github.epicgo.sconey.element.SconeyElementMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.event.ScoreboardUpdateEvent;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.queue.Queue;
import rip.diamond.practice.queue.QueueProfile;
import rip.diamond.practice.queue.QueueType;
import rip.diamond.practice.util.CC;

import java.util.List;
import java.util.Arrays;

public class ScoreboardAdapter implements SconeyElementAdapter {

    private final Eden plugin = Eden.INSTANCE;
    private int titleIndex = 0;
    private final int interval = Config.UPDATE_INTERVAL.toInteger();


    private final List<String> animatedTitles = Language.SCOREBOARD_TITLE.toStringList();

    public ScoreboardAdapter() {
        startTitleAnimation();
    }

    private void startTitleAnimation() {
        new BukkitRunnable() {
            @Override
            public void run() {
                titleIndex = (titleIndex + 1) % animatedTitles.size();
            }
        }.runTaskTimer(plugin, 1, interval);
    }

    @Override
    public SconeyElement getElement(final Player player) {
        SconeyElement element = new SconeyElement();

        PlayerProfile profile = PlayerProfile.get(player);
        if (profile == null) {
            return element;
        }
        ChatColor def = CC.getColorFromName(Config.DEFAULT_THEME.toString());
        Object theme = profile.getSettings().get(ProfileSettings.THEME_SELECTION);
        if (theme == null) {
            theme = Config.DEFAULT_THEME;
        }
        ChatColor c = CC.getColorFromName(theme.toString());
        if (c == null) {
            c = def;
        }
        element.setTitle(animatedTitles.get(titleIndex).replace("<theme>", c.toString()));

        element.setMode(SconeyElementMode.CUSTOM);

        ScoreboardUpdateEvent event = new ScoreboardUpdateEvent(player);
        event.call();
        if (!event.getLayout().isEmpty()) {
            element.addAll(event.getLayout());
            return element;
        }


        Party party = Party.getByPlayer(player);
        QueueProfile qProfile = Queue.getPlayers().get(player.getUniqueId());
        Match match = profile.getMatch();
        EdenEvent edenEvent = EdenEvent.getOnGoingEvent();

        if (profile.getPlayerState() == PlayerState.LOADING) {
            element.addAll(Language.SCOREBOARD_LOADING.toStringList(player));
        } else if (profile.getPlayerState() == PlayerState.IN_LOBBY && party == null) {
            element.addAll(Language.SCOREBOARD_IN_LOBBY.toStringList(player));
        } else if (profile.getPlayerState() == PlayerState.IN_LOBBY) {
            element.addAll(Language.SCOREBOARD_IN_PARTY.toStringList(player));
        } else if (profile.getPlayerState() == PlayerState.IN_EDIT) {
            element.addAll(Language.SCOREBOARD_IN_EDIT.toStringList(player));
        } else if (profile.getPlayerState() == PlayerState.IN_QUEUE && qProfile != null && qProfile.getQueueType() == QueueType.UNRANKED) {
            element.addAll(Language.SCOREBOARD_IN_QUEUE_UNRANKED.toStringList(player));
        } else if (profile.getPlayerState() == PlayerState.IN_QUEUE && qProfile != null && qProfile.getQueueType() == QueueType.RANKED) {
            element.addAll(Language.SCOREBOARD_IN_QUEUE_RANKED.toStringList(player));
        } else if (edenEvent != null && edenEvent.getTotalPlayers().contains(player) && edenEvent.getInGameScoreboard(player) != null) {
            element.addAll(edenEvent.getInGameScoreboard(player));
        } else if (profile.getPlayerState() == PlayerState.IN_MATCH && match != null) {
            if (!profile.getSettings().get(ProfileSettings.MATCH_SCOREBOARD).isEnabled()) {
                return element;
            }
            element.addAll(match.getMatchScoreboard(player));
        } else if (profile.getPlayerState() == PlayerState.IN_SPECTATING && match != null) {
            if (!profile.getSettings().get(ProfileSettings.MATCH_SCOREBOARD).isEnabled()) {
                return element;
            }
            element.addAll(match.getSpectateScoreboard(player));
        }

        return element;
    }
}
