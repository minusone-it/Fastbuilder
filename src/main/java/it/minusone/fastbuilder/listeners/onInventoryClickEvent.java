package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.gui.islandSelectorGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class onInventoryClickEvent implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && clickedInventory.getHolder() instanceof islandSelectorGui) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() == Material.SKULL_ITEM) {
                Player player = (Player) event.getWhoClicked();
                ItemMeta itemMeta = clickedItem.getItemMeta();

                String islandName = itemMeta.getDisplayName().replace("§r§7Island ", "");
                String playerName = itemMeta.getLore() != null && !itemMeta.getLore().isEmpty()
                        ? itemMeta.getLore().get(0).replace("§r§cOccupied by ", "").replace("§r§2Not occupied!", "")
                        : "N/A";
                String mode = Main.getInstance().modesWherePlayerIn.get(Bukkit.getPlayer(playerName));

                if (playerName.isEmpty())
                    Bukkit.getPlayer(playerName).performCommand("fb join " + mode + " " + islandName);
                else
                    player.sendMessage(Main.getInstance().PREFIX + "§cThe island " + islandName + " is occupied by " + playerName + "!");
            }
        }
    }
}
