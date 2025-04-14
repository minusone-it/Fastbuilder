package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.gui.IslandSelectorGUI;
import it.minusone.fastbuilder.gui.ReplayGUI;
import it.minusone.fastbuilder.gui.SettingsGUI;
import it.minusone.fastbuilder.items.IslandSelectorItem;
import it.minusone.fastbuilder.items.ReplayItem;
import it.minusone.fastbuilder.items.SettingsItem;
import it.minusone.fastbuilder.replay.ReplayManager;
import it.minusone.fastbuilder.replay.ReplayPlayback;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;

public class onPlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock().getType() == Material.GOLD_PLATE) {
                Player player = event.getPlayer();
                Main.getInstance().timer.scoreTime(player);
                Main.getInstance().getConfigManager().addSuccessfulAttempt(player);
                if (!Main.getInstance().playersInPracticeMode.get(event.getPlayer().getUniqueId())) {
                    Main.getInstance().stats.get(player).setName(2, "Personal Best: " + new DecimalFormat("0.000").format(Main.getInstance().getConfigManager().getPB(player.getUniqueId(), Main.getInstance().mapWherePlayerIn.get(player).split("-")[0])).replace(",", "."));
                    Main.getInstance().stats.get(player).setName(1, "Total Attempts: " + Main.getInstance().getConfigManager().getAttempts(player));
                    Main.getInstance().stats.get(player).setName(0, "Successful Attempts: " + Main.getInstance().getConfigManager().getSuccessfulAttempts(player));
                }
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Island Selector"))
                event.getPlayer().openInventory(new IslandSelectorGUI(event.getPlayer()).getInventory());
            if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Settings"))
                event.getPlayer().openInventory(new SettingsGUI().getInventory());
            if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Replay"))
                event.getPlayer().openInventory(new ReplayGUI(event.getPlayer().getUniqueId(), 0).getInventory());
            if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Cancel")) {
                if (Main.currentPlayerReplay.containsKey(event.getPlayer().getUniqueId())) {
                    ReplayManager.endReplay(event.getPlayer());
                    event.getPlayer().teleport(Main.getInstance().getConfigManager().getSpawnLocation(Main.getInstance().mapWherePlayerIn.get(event.getPlayer()).split("-")[0], Main.getInstance().mapWherePlayerIn.get(event.getPlayer()).split("-")[1]));
                    event.getPlayer().getInventory().clear();
                    ItemStack block = Main.getInstance().getConfigManager().getCurrentPlayerBlock(event.getPlayer());
                    block.setAmount(64);
                    event.getPlayer().getInventory().setItem(0, block);
                    event.getPlayer().getInventory().setItem(1, block);
                    event.getPlayer().getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
                    event.getPlayer().getInventory().setItem(4, new IslandSelectorItem());
                    event.getPlayer().getInventory().setItem(6, new ReplayItem());
                    event.getPlayer().getInventory().setItem(7, new SettingsItem());
                }
            } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Stopped")) {
                ItemStack start_stop = new ItemStack(Material.INK_SACK, 1, (short) 10);
                ItemMeta meta = start_stop.getItemMeta();
                meta.setDisplayName("§aRunning §7(Right-Click to use)");
                start_stop.setItemMeta(meta);
                event.getPlayer().getInventory().setItem(4, start_stop);
                ReplayManager.runReplay(event.getPlayer());
            } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Running")) {
                ItemStack start_stop = new ItemStack(Material.INK_SACK, 1, (short) 1);
                ItemMeta meta = start_stop.getItemMeta();
                meta.setDisplayName("§cStopped §7(Right-Click to use)");
                start_stop.setItemMeta(meta);
                event.getPlayer().getInventory().setItem(4, start_stop);
                ReplayManager.pauseReplay(event.getPlayer());
            } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("+1 Tick")) {
                ReplayPlayback replay = Main.currentPlayerReplay.get(event.getPlayer().getUniqueId());
                if (replay.getCurrentTick() <= replay.getReplay().getLength())
                    replay.nextTick();
            } else if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("-1 Tick")) {
                ReplayPlayback replay = Main.currentPlayerReplay.get(event.getPlayer().getUniqueId());
                if (replay.getCurrentTick() >= 0)
                    replay.lastTick();
            }
        }
    }
}
