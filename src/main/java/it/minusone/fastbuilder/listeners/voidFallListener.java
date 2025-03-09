package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class voidFallListener implements Listener {
    @EventHandler
    public void onVoidFall(PlayerMoveEvent event) {
        try {
            Player player = event.getPlayer();
            String mode = Main.getInstance().modesWherePlayerIn.get(player);
            String island = Main.getInstance().islandWherePlayerIn.get(player);
            if (player.getLocation().getY() < (Main.getInstance().getConfigManager().getSpawnLocation(mode, island).getY() - 5)) {
                player.teleport(Main.getInstance().getConfigManager().getSpawnLocation(mode, island));
                player.setNoDamageTicks(20);
                player.getInventory().clear();
                player.getInventory().setItem(0, new ItemStack(Material.SANDSTONE, 64));
                List<Location> playerBlocks = Main.getInstance().placedBlocks.get(player.getUniqueId());
                if (playerBlocks != null) {
                    for (Location blockLocation : playerBlocks) {
                        if (blockLocation.getBlock().getType() == Material.SANDSTONE) {
                            blockLocation.getBlock().setType(Material.AIR);
                        }
                    }
                    Main.getInstance().placedBlocks.remove(player.getUniqueId());
                }
                Main.getInstance().timer.stopTimer(player);
            }
        } catch (Exception e) {

        }
    }
}
