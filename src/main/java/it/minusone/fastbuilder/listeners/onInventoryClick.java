package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.command.fastbuilder;
import it.minusone.fastbuilder.gui.*;
import it.minusone.fastbuilder.replay.ReplayManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.List;

public class onInventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null) {
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (player.getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
            if (clickedInventory.getHolder() instanceof SettingsGUI) {
                if (clickedItem != null) {
                    if (clickedItem.getType() == Material.DIAMOND_PICKAXE) {
                        if (Main.getInstance().getConfigManager().getCoinsOfPlayer(player) >= Main.getInstance().getConfigManager().getCoinsForResetStats()) {
                            player.openInventory(new ConfirmGUI().getInventory());
                        } else {
                            player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "You don't have enough coins to reset stats");
                        }
                    } else if (clickedItem.getType() == Material.SANDSTONE) {
                        player.openInventory(new BlockSelectorGUI(player, 0).getInventory());
                    } else if (clickedItem.getType() == Material.STAINED_CLAY) {
                        if (!Main.getInstance().playersInPracticeMode.get(player.getUniqueId())) {
                            player.getInventory().setItem(3, new ItemStack(Material.STAINED_CLAY, 64));
                            Main.getInstance().playersInPracticeMode.put(player.getUniqueId(), true);
                        } else {
                            player.getInventory().setItem(3, null);
                            if (Main.getInstance().placedPracticeBlocks.get(player.getUniqueId()) != null) {
                                for (Location loc : Main.getInstance().placedPracticeBlocks.get(player.getUniqueId()))
                                    loc.getBlock().setType(Material.AIR);
                            }
                            Main.getInstance().playersInPracticeMode.put(player.getUniqueId(), false);
                            player.teleport(Main.getInstance().getConfigManager().getSpawnLocation(Main.getInstance().mapWherePlayerIn.get(player).split("-")[0], Main.getInstance().mapWherePlayerIn.get(player).split("-")[1]));
                        }
                        player.closeInventory();
                    }
                }
            } else if (clickedInventory.getHolder() instanceof IslandSelectorGUI) {
                if (clickedItem != null) {
                    ItemMeta itemMeta = clickedItem.getItemMeta();

                    String islandName = itemMeta.getDisplayName().replace("§r§7Island ", "");
                    String playerName = itemMeta.getLore() != null && !itemMeta.getLore().isEmpty()
                            ? itemMeta.getLore().get(0).replace("§r§cOccupied by ", "").replace("§r§2Not occupied!", "")
                            : "N/A";
                    String mode = Main.getInstance().mapWherePlayerIn.get(player).split("-")[0];
                    if (playerName.isEmpty())
                        fastbuilder.teleportPlayerToIslandB(player, mode, islandName);
                    else
                        player.sendMessage(Main.getInstance().PREFIX + "§cThe island " + islandName + " is occupied by " + playerName + "!");
                }
            } else if (clickedInventory.getHolder() instanceof ReplayGUI) {
                ReplayGUI.handleNavigation(player, clickedItem, player.getUniqueId(), Main.getInstance().page.get(player.getUniqueId()));
                if (clickedItem.getItemMeta() != null && clickedItem.getItemMeta().getDisplayName().contains("Replay")) {
                    Main.getInstance().timer.stopTimer(player, false);
                    List<Location> playerBlocks = Main.getInstance().placedBlocks.get(player.getUniqueId());
                    if (playerBlocks != null) {
                        for (Location blockLocation : playerBlocks)
                            blockLocation.getBlock().setType(Material.AIR);
                    }
                    if (!clickedItem.getItemMeta().getDisplayName().contains("Last")) {
                        String a = clickedItem.getItemMeta().getDisplayName().replace("Replay: ", "");
                        String b = a + ".replay";
                        File replayFile = new File("plugins/Fastbuilder/replays/" + player.getUniqueId() + "/" + Main.getInstance().mapWherePlayerIn.get(player).split("-")[0], b);
                        if (replayFile.exists()) {
                            player.closeInventory();
                            player.getInventory().clear();
                            ReplayManager.playReplay(player, replayFile);
                        }
                    } else {
                        player.closeInventory();
                        ReplayManager.playReplay(player, Main.getInstance().lastReplay.get(player.getUniqueId()));
                    }
                }
            } else if (clickedInventory.getHolder() instanceof ModeChangerGUI) {
                if (clickedItem != null && clickedItem.getItemMeta() != null) {
                    String mode = clickedItem.getItemMeta().getDisplayName().replaceAll("§[0-9a-fA-Fk-oK-r]", "");
                    String modeA = Main.getInstance().mapWherePlayerIn.get(player);
                    if (modeA != null) {
                        if (!modeA.split("-")[0].equals(mode))
                            player.performCommand("fb join " + mode);
                        else
                            player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "You are already in this mode!");
                    }
                }
            } else if (clickedInventory.getHolder() instanceof BlockSelectorGUI) {
                if (clickedItem != null && clickedItem.getItemMeta() != null) {
                    int cost = Integer.parseInt(clickedItem.getItemMeta().getLore().get(0).replace(ChatColor.YELLOW + "Cost:", "").replace("coins", "").replace(" ", ""));
                    if (Main.getInstance().getConfigManager().getCoinsOfPlayer(player) > cost) {
                        if (!Main.getInstance().getConfigManager().hasPlayerGotBlock(player, clickedItem)) {
                            player.closeInventory();
                            short damageValue = clickedItem.getDurability();
                            Main.getInstance().getConfigManager().setCoinsOfPlayer(player, Main.getInstance().getConfigManager().getCoinsOfPlayer(player) - cost);
                            Main.getInstance().getConfigManager().addBlockToPlayer(clickedItem, player);
                            Main.getInstance().getConfigManager().setCurrentPlayerBlock(player, clickedItem);
                            ItemStack block = new ItemStack(Material.getMaterial(clickedItem.getItemMeta().getDisplayName().replace(ChatColor.YELLOW + "", "")), 64);
                            block.setDurability(damageValue);
                            player.getInventory().setItem(0, block);
                            player.getInventory().setItem(1, block);
                            player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "You successfully bought the block.");
                        } else {
                            if (Main.getInstance().getConfigManager().hasPlayerGotBlock(player, clickedItem)) {
                                Main.getInstance().getConfigManager().setCurrentPlayerBlock(player, clickedItem);
                                player.closeInventory();
                                short damageValue = clickedItem.getDurability();
                                ItemStack block = new ItemStack(Material.getMaterial(clickedItem.getItemMeta().getDisplayName().replace(ChatColor.YELLOW + "", "")), 64);
                                block.setDurability(damageValue);
                                player.getInventory().setItem(0, block);
                                player.getInventory().setItem(1, block);
                                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Set " + Material.getMaterial(clickedItem.getItemMeta().getDisplayName().replace(ChatColor.YELLOW + "", "")) + " as your current block.");
                            }
                        }
                    } else {
                        if (Main.getInstance().getConfigManager().hasPlayerGotBlock(player, clickedItem)) {
                            Main.getInstance().getConfigManager().setCurrentPlayerBlock(player, clickedItem);
                            player.closeInventory();
                            short damageValue = clickedItem.getDurability();
                            ItemStack block = new ItemStack(Material.getMaterial(clickedItem.getItemMeta().getDisplayName().replace(ChatColor.YELLOW + "", "")), 64);
                            block.setDurability(damageValue);
                            player.getInventory().setItem(0, block);
                            player.getInventory().setItem(1, block);
                            player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Set " + Material.getMaterial(clickedItem.getItemMeta().getDisplayName().replace(ChatColor.YELLOW + "", "")) + " as your current block.");
                        } else {
                            player.closeInventory();
                            player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "You don't have enough money to buy it!");
                        }
                    }
                }
            } else if (event.getClickedInventory().getHolder() instanceof ConfirmGUI) {
                if (clickedItem != null) {
                    if (clickedItem.getItemMeta().getDisplayName().contains("Confirm")) {
                        Main.getInstance().getConfigManager().deleteStatsOfPlayer(player);
                        Main.getInstance().saveConfig();
                        Main.getInstance().getConfigManager().setCoinsOfPlayer(player, Main.getInstance().getConfigManager().getCoinsOfPlayer(player) - Main.getInstance().getConfigManager().getCoinsForResetStats());
                        Main.getInstance().scoreboardManager.updateScoreboard(player, "0.000", 0.000);
                        player.closeInventory();
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Successfully resetted stats!");
                    } else {
                        player.closeInventory();
                        player.openInventory(new SettingsGUI(player).getInventory());
                    }
                }
            }
        }
    }
}
