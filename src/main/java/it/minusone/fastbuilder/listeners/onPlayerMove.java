package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.IslandSelectorItem;
import it.minusone.fastbuilder.items.LobbyItem;
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
                String mode = Main.getInstance().mapWherePlayerIn.get(player).split("-")[0];
                String island = Main.getInstance().mapWherePlayerIn.get(player).split("-")[1];
                if (!Main.getInstance().getConfigManager().isModeInclined(mode)) {
                    if ((int) player.getLocation().getY() == Main.getInstance().getConfigManager().getSpawnLocation(mode, island).getY() - 4 || (int) player.getLocation().getX() == (int) Main.getInstance().getConfigManager().getSpawnLocation(mode, island).getX() - Main.getInstance().getConfigManager().getMaxDistance() || (int) player.getLocation().getX() == (int) Main.getInstance().getConfigManager().getSpawnLocation(mode, island).getX() + Main.getInstance().getConfigManager().getMaxDistance() || (int) player.getLocation().getZ() == (int) Main.getInstance().getConfigManager().getSpawnLocation(mode, island).getZ() - Main.getInstance().getConfigManager().getMaxDistance()) {
                        player.teleport(Main.getInstance().getConfigManager().getSpawnLocation(mode, island));
                        if (!Main.currentPlayerReplay.containsKey(player.getUniqueId())) {
                            player.getInventory().clear();
                            ItemStack block = Main.getInstance().getConfigManager().getCurrentPlayerBlock(player);
                            block.setAmount(64);
                            player.getInventory().setItem(0, block);
                            player.getInventory().setItem(1, block);
                            player.getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
                            if (Main.getInstance().playersInPracticeMode.get(player.getUniqueId()))
                                player.getInventory().setItem(3, new ItemStack(Material.STAINED_CLAY, 64));
                            player.getInventory().setItem(4, new IslandSelectorItem());
                            player.getInventory().setItem(6, new ReplayItem());
                            player.getInventory().setItem(7, new SettingsItem());
                            if (Main.getInstance().getConfigManager().isBungeeModeEnabled())
                                player.getInventory().setItem(8, new LobbyItem());
                            List<Location> playerBlocks = Main.getInstance().placedBlocks.get(player.getUniqueId());
                            if (playerBlocks != null) {
                                for (Location blockLocation : playerBlocks) {
                                    blockLocation.getBlock().setType(Material.AIR);
                                }
                                Main.getInstance().getConfigManager().addAttempt(player);
                                Main.getInstance().placedBlocks.remove(player.getUniqueId());
                                for (int i = 0; i < Main.getInstance().getConfigManager().getHoloLines(player).length; i++) {
                                    Main.getInstance().stats.get(player).setName(
                                            i,
                                            Main.getInstance().getConfigManager().getHoloLines(player)[i]
                                    );
                                }
                            }
                            Main.getInstance().timer.stopTimer(player, false);
                            Main.getInstance().timer.setAlreadyTeleported(player, true);
                        }
                    }
                    if (Main.getInstance().getConfigManager().isJumpPlateEnabled()) {
                        List<Location> locs = new ArrayList<>();
                        for (Location loc : Main.plateAboveLocations) {
                            for (int i = 1; i <= 5; i++) {
                                locs.add(loc.clone().add(0, i, 0));
                            }
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
                            if (playerLoc.equals(loc) && Main.getInstance().mapWherePlayerIn.containsKey(player)) {
                                if (!Main.getInstance().playersInPracticeMode.get(player.getUniqueId())) {
                                    Main.getInstance().timer.scoreTime(player, false);
                                    Main.getInstance().getConfigManager().addAttempt(player);
                                    Main.getInstance().getConfigManager().addSuccessfulAttempt(player);
                                    for (int i = 0; i < Main.getInstance().getConfigManager().getHoloLines(player).length; i++) {
                                        Main.getInstance().stats.get(player).setName(
                                                i,
                                                Main.getInstance().getConfigManager().getHoloLines(player)[i]
                                        );
                                    }
                                } else {
                                    Main.getInstance().timer.scoreTime(player, true);
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {

            }
        }
    }
}
