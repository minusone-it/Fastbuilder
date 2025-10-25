package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.utils.c;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ModeChangerGUI implements InventoryHolder {
    private static int page;
    private final Inventory inventory;

    public ModeChangerGUI(Player requestedPlayer)
    {
        ModeChangerGUI.page = c.pagesOfModeChanger.get(requestedPlayer);
        this.inventory = Bukkit.createInventory(this, Main.getInstance().getConfigManager().getModeChangerGuiMaxSlots(), Main.getInstance().getConfigManager().getModeChangerGuiName().replace("%page%", String.valueOf(ModeChangerGUI.page + 1)));
        this.addItems();
    }

    public static boolean handleNavigation(Player player, ItemStack clickedItem) {
            if (clickedItem.getItemMeta().getDisplayName().contains(Main.getInstance().getConfigManager().previousPage().getItemMeta().getDisplayName())) {
                c.pagesOfModeChanger.put(player, page - 1);
                player.openInventory(new ModeChangerGUI(player).getInventory());
                return true;
            } else if (clickedItem.getItemMeta().getDisplayName().contains(Main.getInstance().getConfigManager().nextPage().getItemMeta().getDisplayName())) {
                c.pagesOfModeChanger.put(player, page + 1);
                player.openInventory(new ModeChangerGUI(player).getInventory());
                return true;
            }
            return false;
    }

    private void addItems() {
        for (int i = 0; i < Main.getInstance().getConfigManager().getItemStacksOfModeChangerGui(page + 1).size(); i++) {
            this.getInventory().setItem(i, Main.getInstance().getConfigManager().getItemStacksOfModeChangerGui(page + 1).get(i));
        }
        if (Main.getInstance().getConfigManager().getMaxPageOfModeChangerGui() > 1 && page < Main.getInstance().getConfigManager().getMaxPageOfModeChangerGui() - 1)
            this.getInventory().setItem(Main.getInstance().getConfigManager().getModeChangerGuiMaxSlots() - 1, Main.getInstance().getConfigManager().nextPage());
        if (page != 0)
            this.getInventory().setItem(Main.getInstance().getConfigManager().getModeChangerGuiMaxSlots() - 9, Main.getInstance().getConfigManager().previousPage());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}