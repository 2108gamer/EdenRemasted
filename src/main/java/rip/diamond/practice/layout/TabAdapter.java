package rip.diamond.practice.layout;

import dev.hely.tab.api.TabColumn;
import dev.hely.tab.api.TabLayout;
import dev.hely.tab.api.TabProvider;
import dev.hely.tab.api.skin.Skin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.util.CC;

import java.util.*;
import java.util.stream.Collectors;

public class TabAdapter implements TabProvider {

    private final Eden eden;

    public TabAdapter(Eden eden) {
        this.eden = eden;
    }

    @Override
    public Set<TabLayout> getProvider(Player player) {
        Set<TabLayout> layoutSet = new HashSet<>();
        String tablistType = eden.getTablistFile().getString("tablist.type");
        List<UUID> sorted = Bukkit.getOnlinePlayers().stream().map(
                        Player::getUniqueId)
                .collect(Collectors.toList());

        for (int i = 1; i <= 20; i++) {
            if (tablistType.equals("VANILLA")) {
                int playerSize = 0;
                int column = 0;
                int row = 1;

                for (UUID uuid : sorted) {
                    Player online = Bukkit.getPlayer(uuid);
                    playerSize++;
                    if (playerSize >= 60) break;

                    String prefix = eden.getTablistFile().getString("tablist.player_prefix");

                    layoutSet.add(new TabLayout(TabColumn.getColumn(column++), row)
                            .setText(CC.translate(Language.translate(prefix + online.getName(), online)))
                            .setSkin(Skin.getSkin(online)));

                    if (column == 4) {
                        column = 0;
                        row++;
                    }
                }
            } else if (tablistType.equals("CUSTOM")) {
                layoutSet.add(new TabLayout(TabColumn.LEFT, i)
                        .setText(CC.translate(Language.translate(getLines("left", i, "text"), player)))
                        .setSkin(getSkin(player, getLines("left", i, "head"))));
                layoutSet.add(new TabLayout(TabColumn.MIDDLE, i)
                        .setText(CC.translate(Language.translate(getLines("middle", i, "text"), player)))
                        .setSkin(getSkin(player, getLines("middle", i, "head"))));
                layoutSet.add(new TabLayout(TabColumn.RIGHT, i)
                        .setText(CC.translate(Language.translate(getLines("right", i, "text"), player)))
                        .setSkin(getSkin(player, getLines("right", i, "head"))));
                layoutSet.add(new TabLayout(TabColumn.FAR_RIGHT, i)
                        .setText(CC.translate(Language.translate(getLines("far_right", i, "text"), player)))
                        .setSkin(getSkin(player, getLines("far_right", i, "head"))));
            }
        }

        return layoutSet;
    }

    @Override
    public List<String> getFooter(Player player) {
        return headerFooterList(eden.getTablistFile().getStringList("tablist.footer"), player);
    }

    @Override
    public List<String> getHeader(Player player) {
        List<String> headerList = eden.getTablistFile().getStringList("tablist.header");
        return headerFooterList(headerList, player);
    }

    public Skin getSkin(Player player, String skinTab) {
        Skin skinDefault = Skin.DEFAULT;

        if (skinTab.contains("%PLAYER%")) {
            skinDefault = Skin.getSkin(player);
        }
        if (skinTab.contains("%DISCORD%")) {
            skinDefault = Skin.DISCORD_SKIN;
        }
        if (skinTab.contains("%YOUTUBE%")) {
            skinDefault = Skin.YOUTUBE_SKIN;
        }
        if (skinTab.contains("%TWITTER%")) {
            skinDefault = Skin.TWITTER_SKIN;
        }
        if (skinTab.contains("%FACEBOOK%")) {
            skinDefault = Skin.FACEBOOK_SKIN;
        }
        if (skinTab.contains("%STORE%")) {
            skinDefault = Skin.STORE_SKIN;
        }
        return skinDefault;
    }

    private List<String> headerFooterList(List<String> path, Player player) {
        List<String> list = new ArrayList<>();

        for (String str : path) {
            list.add(CC.translate(Language.translate(str, player)));
        }
        return list;
    }

    private String getLines(String column, int position, String textOrHead) {
        return eden.getTablistFile().getString("tablist.lines." + column + "." + position + "." + textOrHead);
    }
}
