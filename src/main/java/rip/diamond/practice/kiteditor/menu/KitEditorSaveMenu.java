package rip.diamond.practice.kiteditor.menu;

import lombok.AllArgsConstructor;
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
import rip.diamond.practice.profile.data.ProfileKitData;
import rip.diamond.practice.profile.procedure.Procedure;
import rip.diamond.practice.profile.procedure.ProcedureType;
import rip.diamond.practice.util.BukkitReflection;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

        buttons.put(0, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.WOOL)
                        .durability(5)
                        .name(Language.KIT_EDITOR_SAVE_MENU_SAVE_LOADOUT_BUTTON_NAME.toString(kitLoadoutCustomName))
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                KitLoadout loadout = new KitLoadout(kitLoadoutCustomName, kit);
                loadout.setContents(player.getInventory().getContents());
                profile.getKitData().get(kit.getName()).replaceKit(index, loadout);
                new KitEditorSelectKitMenu().openMenu(player);
                plugin.getKitEditorManager().leaveKitEditor(player, true);
            }
        });

        if (kitLoadout != null) {
            buttons.put(3, new Button() {
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

            ProfileKitData data = profile.getKitData().get(kit.getName());
            KitLoadout load = data.getLoadout(0);
            buttons.put(18, new ArmorDisplayButton(load.getArmor()[3]));
            buttons.put(27, new ArmorDisplayButton(load.getArmor()[2]));
            buttons.put(36, new ArmorDisplayButton(load.getArmor()[1]));
            buttons.put(45, new ArmorDisplayButton(load.getArmor()[0]));


            buttons.put(6, new Button() {
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
                        if (message.contains("&")) {
                            CC.translate(message);
                        }
                        kitLoadout.setCustomName(message);
                        new KitEditorSaveMenu(kit).openMenu(player);
                    });
                }
            });

            buttons.put(8, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.WOOL)
                            .durability(14)
                            .name("&aDelete this kit")
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    profile.getKitData().get(kit.getName()).deleteKit(index);
                    new KitEditorSelectKitMenu().openMenu(player);
                    plugin.getKitEditorManager().leaveKitEditor(player, true);
                }
            });

        }

        int[] extraItemSlots = {20, 21, 22, 23, 24};
        int initial = 0;

        for (KitExtraItem item : kit.getKitExtraItems()) {
            if (initial >= extraItemSlots.length) {
                break;
            }


            ItemBuilder builder = new ItemBuilder(item.getMaterial())
                    .amount(item.getAmount())
                    .durability(item.getData())
                    .enchantments(item.getEnchantments());

            if (item.getName() != null) {
                builder.name(item.getName());
            }

            if (item.isUnbreakable()) {
                builder.unbreakable();
            }

            int slot = extraItemSlots[initial];

            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return builder.build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    player.setItemOnCursor(builder.build());
                }
            });

            initial++;
        }




        for (int slot = 0; slot < getSize(); slot++) {
            if (!buttons.containsKey(slot)) {
                buttons.put(slot, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(Material.STAINED_GLASS_PANE)
                                .durability((short) 7)
                                .name(" ")
                                .build();
                    }
                });
            }
        }

        return buttons;
    }

    @AllArgsConstructor
    private static class ArmorDisplayButton extends Button {
        private ItemStack itemStack;

        @Override
        public ItemStack getButtonItem(Player player) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                return new ItemStack(Material.AIR);
            }

            return new ItemBuilder(itemStack.clone())
                    .name(CC.AQUA + BukkitReflection.getItemStackName(itemStack))
                    .lore(CC.YELLOW + "This is automatically equipped.")
                    .build();
        }
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public String getTitle(Player player) {
        return Language.KIT_EDITOR_SAVE_MENU_NAME.toString();
    }
}
