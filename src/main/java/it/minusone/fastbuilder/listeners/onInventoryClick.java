package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.gui.IslandSelectorGUI;
import it.minusone.fastbuilder.gui.ModeChangerGUI;
import it.minusone.fastbuilder.gui.ReplayGUI;
import it.minusone.fastbuilder.gui.SettingsGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;

public class onInventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null) {
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedInventory.getHolder() instanceof SettingsGUI) {
                event.setCancelled(true);
                if (clickedItem != null && clickedItem.getType() == Material.DIAMOND_PICKAXE) {
                    if (Main.getInstance().getConfigManager().getCoinsOfPlayer(player) >= Main.getInstance().getConfigManager().getCoinsForResetStats()) {
                        Main.getInstance().getConfig().set("best-times." + player.getUniqueId(), "");
                        Main.getInstance().saveConfig();
                        Main.getInstance().scoreboardManager.updateTimer(player, "0.000", 0.000);
                        Main.getInstance().getConfigManager().setCoinsOfPlayer(player, Main.getInstance().getConfigManager().getCoinsOfPlayer(player) - Main.getInstance().getConfigManager().getCoinsForResetStats());
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Successfully resetted stats!");
                    } else {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "You don't have enough coins to reset stats");
                    }
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
            } else if (clickedInventory.getHolder() instanceof ReplayGUI) {
                event.setCancelled(true);
                if (clickedItem != null && clickedItem.getItemMeta() != null && clickedItem.getItemMeta().getDisplayName().contains("Replay")) {
                    File replayFile = new File("plugins/Fastbuilder/replay", clickedItem.getItemMeta().getDisplayName().replace("Replay: ", ""));
                    if (replayFile.exists()) {
                        player.closeInventory();
                        player.getInventory().clear();
                        ItemStack cancel = new ItemStack(Material.BARRIER, 1);
                        ItemStack start_stop = new ItemStack(Material.INK_SACK, 1, (short) 1);
                        ItemMeta meta = start_stop.getItemMeta();
                        meta.setDisplayName("§cStopped §7(Right-Click to use)");
                        start_stop.setItemMeta(meta);
                        ItemMeta meta1 = cancel.getItemMeta();
                        meta1.setDisplayName("§cCancel §7(Right-Click to use)");
                        cancel.setItemMeta(meta1);
                        player.getInventory().setItem(8, cancel);
                        player.getInventory().setItem(4, start_stop);
                    }
                }
            } else if (clickedInventory.getHolder() instanceof ModeChangerGUI) {
                event.setCancelled(true);
                if (clickedItem != null && clickedItem.getItemMeta() != null) {
                    String mode = clickedItem.getItemMeta().getDisplayName().replaceAll("§[0-9a-fA-Fk-oK-r]", "");
                    if (!Main.getInstance().modesWherePlayerIn.get(player).equals(mode))
                        player.performCommand("fb join " + mode);
                    else
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "You are already in this mode!");
                }
            }
        }
    }
}
