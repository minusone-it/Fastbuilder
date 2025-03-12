package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.IslandSelector;
import it.minusone.fastbuilder.items.ReplayItem;
import it.minusone.fastbuilder.items.SettingsItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class onPlayerMove implements Listener {

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        player.setNoDamageTicks(20);
        try {
            String mode = Main.getInstance().modesWherePlayerIn.get(player);
            String island = Main.getInstance().islandWherePlayerIn.get(player);
            if (player.getLocation().getY() < (Main.getInstance().getConfigManager().getSpawnLocation(mode, island).getY() - 4) || (int) player.getLocation().getX() == (int) Main.getInstance().getConfigManager().getSpawnLocation(mode, island).getX() - 4 || (int) player.getLocation().getX() == (int) Main.getInstance().getConfigManager().getSpawnLocation(mode, island).getX() + 4) {
                player.teleport(Main.getInstance().getConfigManager().getSpawnLocation(mode, island));
                player.getInventory().clear();
                player.getInventory().setItem(0, new ItemStack(Material.SANDSTONE, 64));
                player.getInventory().setItem(1, new ItemStack(Material.SANDSTONE, 64));
                player.getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
                player.getInventory().setItem(4, new IslandSelector());
                player.getInventory().setItem(6, new ReplayItem());
                player.getInventory().setItem(7, new SettingsItem());
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
