package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.ModeItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ModeChangerGUI implements InventoryHolder {
    private final Inventory inventory;

    public ModeChangerGUI() {
        this.inventory = Bukkit.createInventory(this, 45, Main.getInstance().getConfigManager().getModeChangerGuiName());
        this.addItems();
    }

    private void addItems() {
        int slot = 0;
        for (String mode : Main.getInstance().getConfigManager().getModes()) {
            this.inventory.addItem(new ModeItem(mode));
            slot++;
            if (slot > inventory.getSize()) break;
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}