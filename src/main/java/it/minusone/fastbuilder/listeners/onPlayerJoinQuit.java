package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.IslandSelector;
import it.minusone.fastbuilder.items.SettingsItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class onPlayerJoinQuit implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Main.getInstance().scoreboardManager.createScoreboard(e.getPlayer(), Main.getInstance().PREFIX);
        e.getPlayer().removePotionEffect(PotionEffectType.SATURATION);
        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 99999, 255, false, false));
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Main.getInstance().scoreboardManager.removeScoreboard(e.getPlayer());
        Main.getInstance().modesWherePlayerIn.remove(e.getPlayer());
        Main.getInstance().islandWherePlayerIn.remove(e.getPlayer());
        List<Location> playerBlocks = Main.getInstance().placedBlocks.get(e.getPlayer().getUniqueId());
        if (playerBlocks != null) {
            for (Location blockLocation : playerBlocks) {
                if (blockLocation.getBlock().getType() == Material.SANDSTONE) {
                    blockLocation.getBlock().setType(Material.AIR);
                }
            }
            Main.getInstance().placedBlocks.remove(e.getPlayer().getUniqueId());
        }
        Main.getInstance().timer.stopTimer(e.getPlayer());
    }

}
