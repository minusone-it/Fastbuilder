package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.gui.IslandSelectorGUI;
import it.minusone.fastbuilder.gui.SettingsGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class onInventoryClickEvent implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null) {
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedInventory.getHolder() instanceof SettingsGUI) {
                event.setCancelled(true);
                if (clickedItem != null && clickedItem.getType() == Material.DIAMOND_PICKAXE) {
                    Main.getInstance().getConfig().set("best-times." + player.getUniqueId(), "");
                    Main.getInstance().saveConfig();
                    Main.getInstance().scoreboardManager.updateTimer(player, "0,000", 0.000);
                    player.sendMessage(Main.getInstance().PREFIX + "Successfully resetted stats!");
                }
            } else if (clickedInventory.getHolder() instanceof IslandSelectorGUI) {
                event.setCancelled(true);

                if (clickedItem != null && clickedItem.getType() == Material.SKULL_ITEM) {
                    ItemMeta itemMeta = clickedItem.getItemMeta();

                    String islandName = itemMeta.getDisplayName().replace("§r§7Island ", "");
                    String playerName = itemMeta.getLore() != null && !itemMeta.getLore().isEmpty()
                            ? itemMeta.getLore().get(0).replace("§r§cOccupied by ", "").replace("§r§2Not occupied!", "")
                            : "N/A";
                    String mode = Main.getInstance().modesWherePlayerIn.get(player);
                    if (playerName.isEmpty())
                        player.performCommand("fb join " + mode + " " + islandName);
                    else
                        player.sendMessage(Main.getInstance().PREFIX + "§cThe island " + islandName + " is occupied by " + playerName + "!");
                }
            }
        }
    }
}
