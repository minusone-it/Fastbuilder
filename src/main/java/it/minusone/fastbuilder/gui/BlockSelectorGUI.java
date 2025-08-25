package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.utils.c;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class BlockSelectorGUI implements InventoryHolder {

    private static int page;
    private final Inventory inventory;
    private final Player requestedPlayer;

    public BlockSelectorGUI(Player requestedPlayer) {
        this.inventory = Bukkit.createInventory(this, Main.getInstance().getConfigManager().getBlockSelectorGuiMaxSlots(), Main.getInstance().getConfigManager().getBlockSelectorGuiName().replace("%page%", String.valueOf(c.pagesOfBlockSelector.get(requestedPlayer) + 1)));
        this.requestedPlayer = requestedPlayer;
        page = c.pagesOfBlockSelector.get(requestedPlayer);
        this.addItems();
    }

    public static void handleNavigation(Player player, ItemStack clickedItem) {
            if (clickedItem.getItemMeta().getDisplayName().contains(Main.getInstance().getConfigManager().previousPage().getItemMeta().getDisplayName())) {
                c.pagesOfBlockSelector.put(player, page - 1);
                player.openInventory(new BlockSelectorGUI(player).getInventory());
            } else if (clickedItem.getItemMeta().getDisplayName().contains(Main.getInstance().getConfigManager().nextPage().getItemMeta().getDisplayName())) {
                c.pagesOfBlockSelector.put(player, page + 1);
                player.openInventory(new BlockSelectorGUI(player).getInventory());
            }
    }

    private void addItems() {
        for (int i = 0; i < Main.getInstance().getConfigManager().getItemStacksOfBlockSelectorGui(requestedPlayer, page + 1).size(); i++) {
            this.getInventory().setItem(i, Main.getInstance().getConfigManager().getItemStacksOfBlockSelectorGui(requestedPlayer, page + 1).get(i));
        }

        if (Main.getInstance().getConfigManager().getMaxPageOfBlockSelectorGui() > 1 && page < Main.getInstance().getConfigManager().getMaxPageOfBlockSelectorGui() - 1)
            this.getInventory().setItem(Main.getInstance().getConfigManager().getBlockSelectorGuiMaxSlots() - 1, Main.getInstance().getConfigManager().nextPage());
        if (page != 0)
            this.getInventory().setItem(Main.getInstance().getConfigManager().getBlockSelectorGuiMaxSlots() - 9, Main.getInstance().getConfigManager().previousPage());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
