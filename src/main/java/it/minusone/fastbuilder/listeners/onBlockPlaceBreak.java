package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.IslandSelector;
import it.minusone.fastbuilder.items.ReplayItem;
import it.minusone.fastbuilder.items.SettingsItem;
import it.minusone.fastbuilder.replay.ReplayManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class onBlockPlaceBreak implements Listener {
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getItemInHand() != null && event.getItemInHand().getItemMeta() != null && event.getItemInHand().getItemMeta().getDisplayName() != null) {
            if (event.getItemInHand().getItemMeta().getDisplayName().contains("Cancel")) {
                event.setCancelled(true);
                ReplayManager.endReplay(event.getPlayer());
                event.getPlayer().getInventory().clear();
                ItemStack block = Main.getInstance().getConfigManager().getCurrentPlayerBlock(event.getPlayer());
                block.setAmount(64);
                event.getPlayer().getInventory().setItem(0, block);
                event.getPlayer().getInventory().setItem(1, block);
                event.getPlayer().getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
                event.getPlayer().getInventory().setItem(4, new IslandSelector());
                event.getPlayer().getInventory().setItem(6, new ReplayItem());
                event.getPlayer().getInventory().setItem(7, new SettingsItem());
                return;
            }
        }
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (Main.getInstance().modesWherePlayerIn.containsKey(event.getPlayer())) {
                Main.getInstance().placedBlocks.computeIfAbsent(event.getPlayer().getUniqueId(), k -> new ArrayList<>()).add(event.getBlock().getLocation());
                if (!Main.getInstance().timer.isPlayerInTimer(event.getPlayer()))
                    Main.getInstance().timer.startTimer(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        List<Location> playerBlocks = Main.getInstance().placedBlocks.get(event.getPlayer().getUniqueId());
        if (playerBlocks != null && playerBlocks.contains(event.getBlock().getLocation()))
            event.setCancelled(false);
        else if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }
}
