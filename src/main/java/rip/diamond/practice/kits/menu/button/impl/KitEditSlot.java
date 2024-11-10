package rip.diamond.practice.kits.menu.button.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.menu.button.KitButton;
import rip.diamond.practice.profile.procedure.Procedure;
import rip.diamond.practice.profile.procedure.ProcedureType;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Menu;

public class KitEditSlot extends KitButton {

    private final Menu backMenu;

    public KitEditSlot(Kit kit, Menu backMenu) {
        super(kit);
        this.backMenu = backMenu;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.ANVIL)
                .name(Language.KIT_BUTTON_EDIT_SLOT_NAME.toString())
                .lore(Language.KIT_BUTTON_EDIT_SLOT_LORE.toStringList(player, kit.getSlot()))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        player.closeInventory();
        Procedure.buildProcedure(player, Language.KIT_BUTTON_EDIT_PRIORITY_PROCEDURE_MESSAGE.toString(), ProcedureType.CHAT, (s) -> {
            String message = (String) s;

            if (Checker.isInteger(message)) {
                Language.INVALID_SYNTAX.sendMessage(player);
                return;
            }

            int KitSlot = Integer.parseInt(message);

            kit.setSlot(KitSlot);
            Language.KIT_BUTTON_EDIT_SLOT_PROCEDURE_SUCCESS.sendMessage(player, kit.getName(), kit.getSlot());
            kit.autoSave();
            backMenu.openMenu(player);
        });

        Language.KIT_BUTTON_EDIT_PRIORITY_PROCEDURE_ADDITIONAL_MESSAGE.sendMessage(player);
    }
}

