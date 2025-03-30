package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.items.BlockSelector;
import it.minusone.fastbuilder.items.ResetItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsGUI implements InventoryHolder {
    public Inventory inventory;

    public SettingsGUI() {
        this.inventory = Bukkit.createInventory(this, 27, "Settings");
        this.addItems(this.getInventory());
    }

    private void addItems(Inventory inventory) {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Coming soon...");
        item.setItemMeta(meta);
        inventory.setItem(11, new BlockSelector());
        inventory.setItem(13, new ResetItem());
        inventory.setItem(15, item);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
