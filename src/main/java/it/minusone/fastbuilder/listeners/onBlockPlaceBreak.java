package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class onBlockPlaceBreak implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Player player = event.getPlayer();
        String mode = Main.getInstance().modesWherePlayerIn.get(player);
        String island = Main.getInstance().islandWherePlayerIn.get(player);
        Location spawnLocation = Main.getInstance().getConfigManager().getSpawnLocation(mode, island);
        Location blockLocation = event.getBlock().getLocation();

        int minX = spawnLocation.getBlockX() - 4;
        int maxX = spawnLocation.getBlockX() + 4;

        if (blockLocation.getBlockX() <= minX || blockLocation.getBlockX() >= maxX) {
            event.setCancelled(true);
            return;
        }

        Main.getInstance().placedBlocks.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(blockLocation);

        if (!Main.getInstance().timer.isPlayerInTimer(player)) {
            Main.getInstance().timer.startTimer(player);
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
