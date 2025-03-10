package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.IslandSelector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class onPlayerJoinQuit implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Main.getInstance().scoreboardManager.createScoreboard(e.getPlayer(), Main.getInstance().PREFIX);
        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 99999, 255, false, false));
        e.getPlayer().getInventory().setItem(4, new IslandSelector());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Main.getInstance().scoreboardManager.removeScoreboard(e.getPlayer());
    }

}
