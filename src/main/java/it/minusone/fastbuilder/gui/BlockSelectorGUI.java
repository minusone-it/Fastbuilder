package it.minusone.fastbuilder.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BlockSelectorGUI implements InventoryHolder {

    private Inventory inventory;

    public BlockSelectorGUI() {
        this.inventory = Bukkit.createInventory(this, 45, "Block Selector");
        this.addItems();
    }

    private void addItems() {
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
