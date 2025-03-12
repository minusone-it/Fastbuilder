package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class ReplayGUI implements InventoryHolder {

    private Inventory inventory;

    public ReplayGUI(UUID playerUUID) {
        this.inventory = Bukkit.createInventory(this, 45, "Replays");
    }

    private void addItems(List<File> replayFiles) {
        for (int i = 0; i < replayFiles.size(); i++) {
            File replayFile = replayFiles.get(i);
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("Replay: " + replayFile.getName());
                item.setItemMeta(meta);
            }
            inventory.setItem(i, item);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
