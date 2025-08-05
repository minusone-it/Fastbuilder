package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.BlockItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BlockSelectorGUI implements InventoryHolder {

    private final Inventory inventory;
    private final Player requestedPlayer;
    private int page;

    public BlockSelectorGUI(Player requestedPlayer, int page) {
        this.inventory = Bukkit.createInventory(this, Main.getInstance().getConfigManager().getBlockSelectorGuiMaxSlots(), Main.getInstance().getConfigManager().getBlockSelectorGuiName());
        this.page = page;
        this.requestedPlayer = requestedPlayer;
        this.addItems();
    }

    private void addItems() {
        
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
