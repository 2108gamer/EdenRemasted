package rip.diamond.practice.profile.divisions.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.profile.divisions.Division;
import rip.diamond.practice.profile.divisions.DivisionManager;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DivisionsMenu extends PaginatedMenu {

    private final Integer[] ALLOWED_SLOT = new Integer[]{
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Divisiones";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        final List<Division> divisions = DivisionManager.getDivisions();
        Common.debug("Divisiones en el menú: " + divisions.size());
        if (divisions.isEmpty()) {
            player.sendMessage("No hay divisiones configuradas.");
            return buttons;
        }

        final int[] index = {0};
        divisions.forEach(division -> {
            if (index[0] < ALLOWED_SLOT.length) {
                buttons.put(ALLOWED_SLOT[index[0]], new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {

                        return new ItemBuilder(Material.valueOf(division.getIcon().toString()))
                                .durability(division.getDurability())
                                .name(division.getDisplayName())
                                .lore(
                                        "&7Victorias mínimas: " + division.getWinsMin(),
                                        "&7Victorias máximas: " + division.getWinsMax(),
                                        division.getMiniLogo()
                                )
                                .build();
                    }


                    public void clicked(Player player, int slot, ClickType clickType) {

                    }
                });
                index[0]++;
            }
        });

        for (int i = 0; i < getSize(); i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(Material.STAINED_GLASS_PANE)
                                .durability(7) // Cristal gris
                                .name(" ") // Sin nombre
                                .build();
                    }


                    public void clicked(Player player, int slot, ClickType clickType) {
                    }
                });
            }
        }

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }
}
