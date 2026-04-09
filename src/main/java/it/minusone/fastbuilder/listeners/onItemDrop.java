package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class onItemDrop implements Listener {
    @EventHandler
    public void onPlayerItemDropEvent(PlayerDropItemEvent event) {
        if (Main.getInstance().mapWherePlayerIn.containsKey(event.getPlayer()))
            event.setCancelled(true);
    }
}
