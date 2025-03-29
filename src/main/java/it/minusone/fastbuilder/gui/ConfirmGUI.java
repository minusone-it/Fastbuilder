package it.minusone.fastbuilder.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfirmGUI implements InventoryHolder {
    public Inventory inventory;

    public ConfirmGUI() {
        this.inventory = Bukkit.createInventory(this, 45);
        ItemStack confirm = new ItemStack(Material.WOOL);
        confirm.setDurability((short) 4);
        ItemMeta a = confirm.getItemMeta();
        a.setDisplayName("§aConfirm");
        ItemStack cancel = new ItemStack(Material.WOOL);
        confirm.setDurability((short) 14);
        ItemMeta b = cancel.getItemMeta();
        b.setDisplayName("§cCancel");
        confirm.setItemMeta(a);
        cancel.setItemMeta(b);
        this.inventory.setItem(21, confirm);
        this.inventory.setItem(19, cancel);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
