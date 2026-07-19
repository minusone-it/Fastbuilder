package it.minusone.fastbuilder.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.gui.IslandSelectorGUI;
import it.minusone.fastbuilder.gui.ReplayGUI;
import it.minusone.fastbuilder.gui.SettingsGUI;
import it.minusone.fastbuilder.items.IslandSelectorItem;
import it.minusone.fastbuilder.items.LobbyItem;
import it.minusone.fastbuilder.items.ReplayItem;
import it.minusone.fastbuilder.items.SettingsItem;
import it.minusone.fastbuilder.replay.ReplayManager;
import it.minusone.fastbuilder.replay.ReplayPlayback;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class onPlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (Main.getInstance().mapWherePlayerIn.containsKey(event.getPlayer())) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Main.getInstance().getConfigManager().getIslandSelectorItemName()))
                    event.getPlayer().openInventory(new IslandSelectorGUI(event.getPlayer()).getInventory());
                if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Main.getInstance().getConfigManager().getSettingsItemName()))
                    event.getPlayer().openInventory(new SettingsGUI(event.getPlayer()).getInventory());
                if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Main.getInstance().getConfigManager().getReplayItemName()))
                    event.getPlayer().openInventory(new ReplayGUI(event.getPlayer().getUniqueId(), 0).getInventory());
                if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Main.getInstance().getConfigManager().getExitReplayItem().getItemMeta().getDisplayName())) {
                    if (Main.currentPlayerReplay.containsKey(event.getPlayer().getUniqueId())) {
                        event.getPlayer().getInventory().clear();
                        onPlayerSneak.resetCamera(event.getPlayer());
                        ReplayManager.endReplay(event.getPlayer());
                        event.getPlayer().teleport(Main.getInstance().getConfigManager().getSpawnLocation(Main.getInstance().mapWherePlayerIn.get(event.getPlayer()).split("-")[0], Main.getInstance().mapWherePlayerIn.get(event.getPlayer()).split("-")[1]));
                        event.getPlayer().getInventory().clear();
                        ItemStack block = Main.getInstance().getConfigManager().getCurrentPlayerBlock(event.getPlayer());
                        block.setAmount(64);
                        event.getPlayer().getInventory().setItem(0, block);
                        event.getPlayer().getInventory().setItem(1, block);
                        event.getPlayer().getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
                        event.getPlayer().getInventory().setItem(4, new IslandSelectorItem());
                        if (Main.getInstance().getConfigManager().isBedItemEnabled())
                            event.getPlayer().getInventory().setItem(5, Main.getInstance().getConfigManager().bedItem());
                        event.getPlayer().getInventory().setItem(6, new ReplayItem());
                        event.getPlayer().getInventory().setItem(7, new SettingsItem());
                        if (Main.getInstance().getConfigManager().isLobbyToggled())
                            event.getPlayer().getInventory().setItem(8, new LobbyItem());
                    }
                } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Main.getInstance().getConfigManager().getStopReplayItem().getItemMeta().getDisplayName())) {
                    event.getPlayer().getInventory().setItem(4, Main.getInstance().getConfigManager().getStartReplayItem());
                    ReplayManager.runReplay(event.getPlayer());
                } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Main.getInstance().getConfigManager().getStartReplayItem().getItemMeta().getDisplayName())) {
                    event.getPlayer().getInventory().setItem(4, Main.getInstance().configManager.getStopReplayItem());
                    ReplayManager.pauseReplay(event.getPlayer());
                } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Main.getInstance().getConfigManager().getNextTickReplayItem().getItemMeta().getDisplayName())) {
                    ReplayPlayback replay = Main.currentPlayerReplay.get(event.getPlayer().getUniqueId());
                    if (replay.getCurrentTick() <= replay.getReplay().getLength())
                        replay.nextTick();
                } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Main.getInstance().getConfigManager().getPrevTickReplayItem().getItemMeta().getDisplayName())) {
                    ReplayPlayback replay = Main.currentPlayerReplay.get(event.getPlayer().getUniqueId());
                    if (replay.getCurrentTick() >= 0)
                        replay.lastTick();
                } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Main.getInstance().getConfigManager().getRestartReplayItem().getItemMeta().getDisplayName())) {
                    ReplayPlayback replay = Main.currentPlayerReplay.get(event.getPlayer().getUniqueId());
                    for (int i = 0; i < replay.getReplay().getLength(); i++) {
                        replay.lastTick();
                    }
                    ReplayManager.runReplay(event.getPlayer());
                    event.getPlayer().getInventory().setItem(4, Main.getInstance().getConfigManager().getStartReplayItem());
                } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Leave")) {
                    if (Main.getInstance().getConfigManager().isBungeeToggled()) {
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Connect");
                        out.writeUTF(Main.getInstance().getConfigManager().getBackendServer());

                        event.getPlayer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
                    } else {
                        for (String command : Main.getInstance().getConfigManager().getCommandsToExecute()) {
                            event.getPlayer().performCommand(command);
                        }
                    }
                } else if (event.getItem() != null && event.getItem().getItemMeta().equals(Main.getInstance().getConfigManager().bedItem().getItemMeta()) && event.getItem().getType().equals(Main.getInstance().getConfigManager().bedItem().getType())) {
                    Player player = event.getPlayer();
                    Main.getInstance().timer.stopTimer(event.getPlayer(), false);
                    player.getInventory().clear();
                    ItemStack block = Main.getInstance().getConfigManager().getCurrentPlayerBlock(player);
                    block.setAmount(64);
                    player.getInventory().setItem(0, block);
                    player.getInventory().setItem(1, block);
                    player.getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
                    if (Main.getInstance().playersInPracticeMode.get(player.getUniqueId()))
                        player.getInventory().setItem(3, new ItemStack(Material.STAINED_CLAY, 64));
                    player.getInventory().setItem(4, new IslandSelectorItem());
                    if (Main.getInstance().getConfigManager().isBedItemEnabled())
                        player.getInventory().setItem(5, Main.getInstance().getConfigManager().bedItem());
                    player.getInventory().setItem(6, new ReplayItem());
                    player.getInventory().setItem(7, new SettingsItem());
                    if (Main.getInstance().getConfigManager().isLobbyToggled())
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
                    event.getPlayer().teleport(Main.getInstance().getConfigManager().getSpawnLocation(Main.getInstance().getModeOfPlayer(event.getPlayer()), Main.getInstance().getIslandOfPlayer(event.getPlayer())));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBreakEvent(BlockBreakEvent event) {
        if (Main.getInstance().mapWherePlayerIn.containsKey(event.getPlayer())) {
            if (event.getBlock() != null) {
                if (event.getBlock().getType().equals(Material.BED_BLOCK)) {
                    Player player = event.getPlayer();
                    if (!Main.getInstance().playersInPracticeMode.get(event.getPlayer().getUniqueId())) {
                        if (Main.getInstance().timer.scoreTime(player, false)) {
                            try {
                                Main.getInstance().getConfigManager().addAttempt(player);
                                Main.getInstance().getConfigManager().addSuccessfulAttempt(player);
                                for (int i = 0; i < Main.getInstance().getConfigManager().getHoloLines(player).length; i++) {
                                    Main.getInstance().stats.get(player).setName(
                                            i,
                                            Main.getInstance().getConfigManager().getHoloLines(player)[i]
                                    );
                                }
                            } catch (Exception e) {
                                System.out.println("An error occoured while trying to save in the config! ID: 1");
                            }
                        }
                    } else
                        Main.getInstance().timer.scoreTime(player, true);
                }
            }
        }
    }
}
