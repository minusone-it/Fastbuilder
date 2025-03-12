package it.minusone.fastbuilder.listeners;

import cf.pies.replay.api.Replay;
import it.minusone.fastbuilder.Main;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class onBlockPlaceBreak implements Listener {
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getItemInHand() != null && event.getItemInHand().getItemMeta() != null && event.getItemInHand().getItemMeta().getDisplayName() != null && event.getItemInHand().getItemMeta().getDisplayName().contains("Cancel"))
            event.setCancelled(true);
        if (event.getBlock().getType().equals(Material.SANDSTONE)) {
            if (!Main.getInstance().timer.isPlayerInTimer(event.getPlayer()))
                Main.getInstance().timer.startTimer(event.getPlayer());
            Main.getInstance().placedBlocks.computeIfAbsent(event.getPlayer().getUniqueId(), k -> new ArrayList<>()).add(event.getBlock().getLocation());
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
