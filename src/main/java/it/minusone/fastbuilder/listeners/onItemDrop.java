package it.minusone.fastbuilder.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class onItemDrop implements Listener {
    @EventHandler
    public void onItemDropEvent(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
