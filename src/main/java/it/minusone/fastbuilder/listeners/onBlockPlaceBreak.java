package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        Player player = event.getPlayer();
        String mode = Main.getInstance().mapWherePlayerIn.get(player).split("-")[0];
        String island = Main.getInstance().mapWherePlayerIn.get(player).split("-")[1];
        Location spawnLocation = Main.getInstance().getConfigManager().getSpawnLocation(mode, island);
        Location blockLocation = event.getBlock().getLocation();

        int minX = spawnLocation.getBlockX() - Main.getInstance().getConfigManager().getMaxDistance();
        int maxX = spawnLocation.getBlockX() + Main.getInstance().getConfigManager().getMaxDistance();

        if (blockLocation.getBlockX() <= minX || blockLocation.getBlockX() >= maxX) {
            event.setCancelled(true);
            return;
        }

        if (!event.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("Practice Blocks"))
            Main.getInstance().placedBlocks.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(blockLocation);
        else
            Main.getInstance().placedPracticeBlocks.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(blockLocation);

        if (!Main.getInstance().timer.isPlayerInTimer(player)) {
            Main.getInstance().timer.startTimer(player);
        }
        if (event.getItemInHand().getAmount() < 10)
            event.getItemInHand().setAmount(64);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        List<Location> playerBlocks = new ArrayList<>();
        if (Main.getInstance().placedBlocks.get(event.getPlayer().getUniqueId()) != null)
            playerBlocks.addAll(Main.getInstance().placedBlocks.get(event.getPlayer().getUniqueId()));

        if (Main.getInstance().placedPracticeBlocks.get(event.getPlayer().getUniqueId()) != null)
            playerBlocks.addAll(Main.getInstance().placedPracticeBlocks.get(event.getPlayer().getUniqueId()));

        if (playerBlocks.contains(event.getBlock().getLocation()))
            event.setCancelled(false);

        else if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }
}
