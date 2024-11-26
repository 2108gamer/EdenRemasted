package rip.diamond.practice.kiteditor.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitExtraItem;
import rip.diamond.practice.kits.KitLoadout;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.procedure.Procedure;
import rip.diamond.practice.profile.procedure.ProcedureType;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class KitEditorSaveMenu extends Menu {
    private final Kit kit;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        PlayerProfile profile = PlayerProfile.get(player);
        Map<Integer, Button> buttons = new HashMap<>();


        int index = 0;
        KitLoadout kitLoadout = profile.getKitData().get(kit.getName()).getLoadouts()[index];
        String kitLoadoutCustomName = kitLoadout == null ? kit.getDisplayName() + "#" + (index + 1) : kitLoadout.getCustomName();


        buttons.put(10, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.CHEST)
                        .name(Language.KIT_EDITOR_SAVE_MENU_SAVE_LOADOUT_BUTTON_NAME.toString(kitLoadoutCustomName))
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                KitLoadout loadout = new KitLoadout(kitLoadoutCustomName, kit);
                loadout.setContents(player.getInventory().getContents());
                profile.getKitData().get(kit.getName()).replaceKit(index, loadout);
                new KitEditorSelectKitMenu().openMenu(player);
                plugin.getKitEditorManager().leaveKitEditor(player,true);



            }
        });


        if (kitLoadout != null) {
            buttons.put(12, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.BOOK)
                            .name(Language.KIT_EDITOR_SAVE_MENU_LOAD_LOADOUT_BUTTON_NAME.toString(kitLoadoutCustomName))
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    player.getInventory().setArmorContents(null);
                    player.getInventory().setContents(kitLoadout.getContents());
                    player.updateInventory();
                }
            });

            buttons.put(14, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.NAME_TAG)
                            .name(Language.KIT_EDITOR_SAVE_MENU_RENAME_LOADOUT_BUTTON_NAME.toString(kitLoadoutCustomName))
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    player.closeInventory();
                    Procedure.buildProcedure(player, Language.KIT_EDITOR_SAVE_MENU_RENAME_INSTRUCTIONS.toString(kitLoadoutCustomName), ProcedureType.CHAT, (string) -> {
                        String message = (String) string;
                        if (!message.matches("[a-zA-Z0-9_\\s+]*")) {
                            Language.KIT_EDITOR_SAVE_MENU_INVALID_CHARACTER.sendMessage(player);
                            return;
                        }
                        kitLoadout.setCustomName(message);
                        new KitEditorSaveMenu(kit).openMenu(player);
                    });
                }
            });

            buttons.put(16, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.LAVA_BUCKET)
                            .name(Language.KIT_EDITOR_SAVE_MENU_DELETE_LOADOUT_BUTTON_NAME.toString(kitLoadoutCustomName))
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    profile.getKitData().get(kit.getName()).deleteKit(index);
                    new KitEditorSelectKitMenu().openMenu(player);
                    plugin.getKitEditorManager().leaveKitEditor(player,true);
                }
            });
            buttons.put(18, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.FEATHER)
                            .name(Language.KIT_EDITOR_SAVE_MENU_DELETE_LOADOUT_BUTTON_NAME.toString(kitLoadoutCustomName))
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    profile.getKitData().get(kit.getName()).deleteKit(index);
                    new KitEditorExtraItemsMenu(kit, player).openMenu(player);

                }
            });
        }

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 4;
    }

    @Override
    public String getTitle(Player player) {
        return Language.KIT_EDITOR_SAVE_MENU_NAME.toString();
    }
}
