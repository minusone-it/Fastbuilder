package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class onPlayerJoinQuit implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Main.getInstance().scoreboardManager.createScoreboard(e.getPlayer(), Main.getInstance().PREFIX);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Main.getInstance().scoreboardManager.removeScoreboard(e.getPlayer());
    }

}
