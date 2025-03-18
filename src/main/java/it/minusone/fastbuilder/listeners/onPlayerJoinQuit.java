package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class onPlayerJoinQuit implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Main.getInstance().scoreboardManager.createScoreboard(e.getPlayer(), Main.getInstance().PREFIX);
        onPlayerClickEntity.injectPacketListener(e.getPlayer());
        if (Main.getInstance().getConfigManager().getCoinsOfPlayer(e.getPlayer()) == 0)
            Main.getInstance().getConfigManager().setCoinsOfPlayer(e.getPlayer(), 0);
        if (!Main.getInstance().getConfigManager().hasPlayerGotBlock(e.getPlayer(), Material.SANDSTONE)) {
            Main.getInstance().getConfigManager().addBlockToPlayer(Material.SANDSTONE, e.getPlayer());
            Main.getInstance().getConfigManager().addBlockToPlayer(Material.RED_SANDSTONE, e.getPlayer());
        }
        if (Main.getInstance().getConfigManager().getCurrentPlayerBlock(e.getPlayer()) == null)
            Main.getInstance().getConfigManager().setCurrentPlayerBlock(e.getPlayer(), Material.SANDSTONE);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Main.getInstance().scoreboardManager.removeScoreboard(e.getPlayer());
        Main.getInstance().removePlayerFromLeaderboard(e.getPlayer(), Main.getInstance().modesWherePlayerIn.get(e.getPlayer()));
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
