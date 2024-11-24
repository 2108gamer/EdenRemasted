package rip.diamond.practice.kiteditor.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.pagination.PageButton;
import rip.diamond.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

public class KitEditorSelectKitMenu extends PaginatedMenu {

	private static final Integer[] ALLOWED_SLOT = new Integer[]{
			10, 11, 12, 13, 14, 15, 16,
			19, 20, 21, 22, 23, 24, 25,
			28, 29, 30, 31, 32, 33, 34
	};

	@Override
	public String getPrePaginatedTitle(Player player) {
		return Language.KIT_EDITOR_SELECT_KIT_MENU_NAME.toString();
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (Kit kit : Kit.getKits()) {
			if (kit.getGameRules().isReceiveKitLoadoutBook() && kit.isEnabled()) {
				buttons.put(buttons.size(), new Button() {
					@Override
					public ItemStack getButtonItem(Player player) {
						return new ItemBuilder(kit.getDisplayIcon().clone())
								.name(Language.KIT_EDITOR_SELECT_KIT_MENU_BUTTON_NAME.toString(kit.getDisplayName()))
								.lore(Language.KIT_EDITOR_SELECT_KIT_MENU_BUTTON_LORE.toStringList(player))
								.build();
					}

					@Override
					public void clicked(Player player, ClickType clickType) {
						player.closeInventory();
						Eden.INSTANCE.getKitEditorManager().addKitEditor(player, kit);
					}
				});
			}
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
			buttons.put(36, new PageButton(-1, this));
		}


		if (endIndex < allButtons.size()) {
			buttons.put(44, new PageButton(1, this));
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
