package rip.diamond.practice.queue.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.queue.Queue;
import rip.diamond.practice.queue.QueueType;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.pagination.PageButton;
import rip.diamond.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class QueueMenu extends PaginatedMenu {

    private final QueueType queueType;


    private final Integer[] ALLOWED_SLOT = new Integer[]{
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    @Override
    public String getPrePaginatedTitle(Player player) {
        return Language.QUEUE_MENU_TITLE.toString(queueType.getReadable());
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        final Kit[] kits = Kit.getKits().stream()
                .filter(Kit::isEnabled)
                .filter(kit -> queueType == QueueType.UNRANKED || kit.isRanked())
                .toArray(Kit[]::new);

        for (int i = 0; i < kits.length; i++) {
            Kit kit = kits[i];
            buttons.put(i, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(kit.getDisplayIcon().clone())
                            .name(kit.getDisplayName())
                            .lore(kit.getDescription())
                            .lore(Language.QUEUE_MENU_BUTTON_LORE.toStringList(player,
                                    Queue.getPlayers().values().stream().filter(profile -> profile.getKit() == kit && profile.getQueueType() == queueType).count(),
                                    Match.getMatches().values().stream().filter(match -> match.getKit() == kit && match.getQueueType() == queueType).mapToInt(match -> match.getMatchPlayers().size()).sum(),
                                    kit.getDisplayName()
                            ))
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    player.closeInventory();
                    Queue.joinQueue(player, kit, queueType);
                }
            });
        }

        return buttons;
    }


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Map<Integer, Button> allButtons = getAllPagesButtons(player);

        int startIndex = (page - 1) * getMaxItemsPerPage(player);
        int endIndex = Math.min(startIndex + getMaxItemsPerPage(player), allButtons.size());

        int kitIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            if (kitIndex < ALLOWED_SLOT.length) {
                buttons.put(ALLOWED_SLOT[kitIndex], allButtons.get(i));
                kitIndex++;
            }
        }

        if (page > 1) {
            buttons.put(40, new PageButton(-1, this));
        }
        if (endIndex < allButtons.size()) {
            buttons.put(40, new PageButton(1, this));
        }


        for (int i = 0; i < getSize(); i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(Material.STAINED_GLASS_PANE)
                                .durability(7)
                                .name(" ")
                                .build();
                    }
                });
            }
        }

        return buttons;
    }


    @Override
    public int getMaxItemsPerPage(Player player) {
        return ALLOWED_SLOT.length;
    }

    @Override
    public int getSize() {
        return 45;
    }
}
