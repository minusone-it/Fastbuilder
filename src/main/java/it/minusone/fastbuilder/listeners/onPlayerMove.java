package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.IslandSelector;
import it.minusone.fastbuilder.items.ReplayItem;
import it.minusone.fastbuilder.items.SettingsItem;
import org.bukkit.GameMode;
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
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            try {
                String mode = Main.getInstance().modesWherePlayerIn.get(player);
                String island = Main.getInstance().islandWherePlayerIn.get(player);
                if (player.getLocation().getY() < (Main.getInstance().getConfigManager().getSpawnLocation(mode, island).getY() - 4) || (int) player.getLocation().getX() == (int) Main.getInstance().getConfigManager().getSpawnLocation(mode, island).getX() - 4 || (int) player.getLocation().getX() == (int) Main.getInstance().getConfigManager().getSpawnLocation(mode, island).getX() + 4) {
                    player.teleport(Main.getInstance().getConfigManager().getSpawnLocation(mode, island));
                    player.getInventory().clear();
                    ItemStack block = Main.getInstance().getConfigManager().getCurrentPlayerBlock(player);
                    block.setAmount(64);
                    player.getInventory().setItem(0, block);
                    player.getInventory().setItem(1, block);
                    player.getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
                    player.getInventory().setItem(4, new IslandSelector());
                    player.getInventory().setItem(6, new ReplayItem());
                    player.getInventory().setItem(7, new SettingsItem());
                    List<Location> playerBlocks = Main.getInstance().placedBlocks.get(player.getUniqueId());
                    if (playerBlocks != null) {
                        for (Location blockLocation : playerBlocks) {
                            blockLocation.getBlock().setType(Material.AIR);
                        }
                        Main.getInstance().placedBlocks.remove(player.getUniqueId());
                    }
                    Main.getInstance().timer.stopTimer(player);
                }
                /**
                 * TODO
                 * for (Location loc : Main.plateAboveLocations) {
                    if (player.getLocation() == loc && Main.getInstance().getConfigManager().isJumpPlateEnabled())
                        Main.getInstance().timer.scoreTime(player);
                }*/
            } catch (Exception ignored) {

            }
        }
    }
}
