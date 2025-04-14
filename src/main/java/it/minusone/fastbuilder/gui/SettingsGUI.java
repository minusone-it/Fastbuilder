package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.items.BlockSelectorItem;
import it.minusone.fastbuilder.items.PracticeModeItem;
import it.minusone.fastbuilder.items.ResetItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class SettingsGUI implements InventoryHolder {
    public Inventory inventory;

    public SettingsGUI() {
        this.inventory = Bukkit.createInventory(this, 27, "Settings");
        this.addItems(this.getInventory());
    }

    private void addItems(Inventory inventory) {
       /* ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Coming soon...");
        item.setItemMeta(meta);*/
        inventory.setItem(11, new BlockSelectorItem());
        inventory.setItem(13, new ResetItem());
        inventory.setItem(15, new PracticeModeItem());
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
