package it.minusone.fastbuilder.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class onEntityDamage implements Listener {
    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }
}
