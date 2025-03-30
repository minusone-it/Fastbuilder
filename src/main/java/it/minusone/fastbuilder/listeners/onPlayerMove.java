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

import java.util.ArrayList;
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
                    Main.getInstance().timer.setAlreadyTeleported(player, true);
                    player.teleport(Main.getInstance().getConfigManager().getSpawnLocation(mode, island));
                    if (!Main.currentPlayerReplay.containsKey(player.getUniqueId())) {
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
                            Main.getInstance().getConfigManager().addAttempt(player);
                            Main.getInstance().placedBlocks.remove(player.getUniqueId());
                            Main.getInstance().stats.get(player).setName(1, "Total Attempts: " + Main.getInstance().getConfigManager().getAttempts(player));
                            Main.getInstance().stats.get(player).setName(0, "Successful Attempts: " + Main.getInstance().getConfigManager().getSuccessfulAttempts(player));
                        }
                        Main.getInstance().timer.stopTimer(player, false);
                    }
                }
                if (Main.getInstance().getConfigManager().isJumpPlateEnabled()) {
                    List<Location> locs = new ArrayList<>();
                    for (Location loc : Main.plateAboveLocations) {
                        Location loc1 = loc.clone().add(0, 1, 0);
                        Location loc2 = loc.clone().add(0, 2, 0);
                        Location loc3 = loc.clone().add(0, 3, 0);

                        locs.add(loc1);
                        locs.add(loc2);
                        locs.add(loc3);
                    }
                    Location playerLoc = player.getLocation();
                    playerLoc.setX(Math.floor(playerLoc.getX()) + 0.5);
                    playerLoc.setY(Math.floor(playerLoc.getY()));
                    playerLoc.setZ(Math.floor(playerLoc.getZ()) + 0.5);
                    playerLoc.setYaw(0);
                    playerLoc.setPitch(0);

                    for (Location loc : locs) {
                        loc.setX(Math.floor(loc.getX()) + 0.5);
                        loc.setY(Math.floor(loc.getY()));
                        loc.setZ(Math.floor(loc.getZ()) + 0.5);
                        if (playerLoc.equals(loc) && Main.getInstance().modesWherePlayerIn.containsKey(player)) {
                            Main.getInstance().timer.scoreTime(player);
                            Main.getInstance().getConfigManager().addAttempt(player);
                            Main.getInstance().getConfigManager().addSuccessfulAttempt(player);
                            Main.getInstance().stats.get(player).setName(1, "Total Attempts: " + Main.getInstance().getConfigManager().getAttempts(player));
                            Main.getInstance().stats.get(player).setName(0, "Successful Attempts: " + Main.getInstance().getConfigManager().getSuccessfulAttempts(player));
                        }
                    }
                }
            } catch (Exception ignored) {

            }
        }
    }
}
