package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;

public class onBlockPlace implements Listener {
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getBlock().getType().equals(Material.SANDSTONE)) {
            if (!Main.getInstance().timer.isPlayerInTimer(event.getPlayer()))
                Main.getInstance().timer.startTimer(event.getPlayer());
            Main.getInstance().placedBlocks.computeIfAbsent(event.getPlayer().getUniqueId(), k -> new ArrayList<>()).add(event.getBlock().getLocation());
        }
    }
}
