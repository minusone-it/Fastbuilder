package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SettingsGUI implements InventoryHolder {
    private final Player incomingPlayer;
    public Inventory inventory;

    public SettingsGUI(Player incomingPlayer) {
        this.inventory = Bukkit.createInventory(this, Main.getInstance().getConfigManager().getSettingsGuiMaxSlots(), Main.getInstance().getConfigManager().getSettingsGuiName());
        this.incomingPlayer = incomingPlayer;
        this.addItems(this.getInventory());
    }

    private void addItems(Inventory inventory) {
        for (int i = 0; i < Main.getInstance().getConfigManager().getItemStacksOfSettingsGui(incomingPlayer).size() - 1; i++) {
            inventory.setItem(i, Main.getInstance().getConfigManager().getItemStacksOfSettingsGui(incomingPlayer).get(i));
        }
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
