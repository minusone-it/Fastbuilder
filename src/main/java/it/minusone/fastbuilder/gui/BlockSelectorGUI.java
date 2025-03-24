package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.BlockItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class BlockSelectorGUI implements InventoryHolder {

    private final Inventory inventory;
    private final Player requestedPlayer;
    public BlockSelectorGUI(Player requestedPlayer) {
        this.inventory = Bukkit.createInventory(this, 45, "Block Selector");
        this.requestedPlayer = requestedPlayer;
        this.addItems();
    }

    private void addItems() {
        int slot = 0;

        List<Material> sortedMaterials = new ArrayList<>(Main.blocks.keySet());
        sortedMaterials.sort(Comparator.comparingInt(Main.blocks::get));

        for (Material material : sortedMaterials) {
            if (material == Material.WOOL) {
                for (byte i = 0; i < 16; i++) {
                    inventory.setItem(slot, new BlockItem(material, requestedPlayer, Main.blocks.get(material), i));
                    slot++;
                    if (slot >= inventory.getSize()) return;
                }
            } else {
                inventory.setItem(slot, new BlockItem(material, requestedPlayer, Main.blocks.get(material)));
                slot++;
                if (slot >= inventory.getSize()) return;
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
