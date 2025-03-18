package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.gui.IslandSelectorGUI;
import it.minusone.fastbuilder.gui.SettingsGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class onPlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock().getType() == Material.GOLD_PLATE) {
                Player player = event.getPlayer();
                Main.getInstance().timer.scoreTime(player);
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Island Selector"))
                event.getPlayer().openInventory(new IslandSelectorGUI(event.getPlayer()).getInventory());
            if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Settings"))
                event.getPlayer().openInventory(new SettingsGUI().getInventory());
                /**
                 * TODO: DO REPLAY HERE
                 * if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Replay"))
                 event.getPlayer().openInventory(new ReplayGUI(event.getPlayer().getUniqueId()).getInventory());
                 if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Cancel")) {
                 if (Main.currentPlayerReplay.containsKey(event.getPlayer().getUniqueId())) {
                 ReplayManager.endReplay(event.getPlayer());
                 event.getPlayer().getInventory().clear();
                 event.getPlayer().getInventory().setItem(0, new ItemStack(Material.SANDSTONE, 64));
                 event.getPlayer().getInventory().setItem(1, new ItemStack(Material.SANDSTONE, 64));
                 event.getPlayer().getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
                 event.getPlayer().getInventory().setItem(4, new IslandSelector());
                 //event.getPlayer().getInventory().setItem(6, new ReplayItem());
                 event.getPlayer().getInventory().setItem(7, new SettingsItem());
                 }
                 } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Stopped")) {
                 ItemStack start_stop = new ItemStack(Material.INK_SACK, 1, (short) 10);
                 ItemMeta meta = start_stop.getItemMeta();
                 meta.setDisplayName("§aRunning §7(Right-Click to use)");
                 start_stop.setItemMeta(meta);
                 event.getPlayer().getInventory().setItem(4, start_stop);
                 ReplayManager.runReplay(event.getPlayer());
                 } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Running")) {
                 ItemStack start_stop = new ItemStack(Material.INK_SACK, 1, (short) 1);
                 ItemMeta meta = start_stop.getItemMeta();
                 meta.setDisplayName("§cStopped §7(Right-Click to use)");
                 start_stop.setItemMeta(meta);
                 event.getPlayer().getInventory().setItem(4, start_stop);
                 ReplayManager.pauseReplay(event.getPlayer());
                 }*/
            }

    }

}
