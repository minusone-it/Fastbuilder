package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.items.ResetItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class SettingsGUI implements InventoryHolder {
    public Inventory inventory;
    public SettingsGUI() {
        this.inventory = Bukkit.createInventory(this, 27, "Settings");
        this.addItems(this.getInventory());
    }

    private void addItems(Inventory inventory) {
        inventory.setItem(15, new ResetItem());
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
