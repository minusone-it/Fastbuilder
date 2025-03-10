package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.gui.islandSelectorGui;
import it.minusone.fastbuilder.items.IslandSelector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class onPlayerInteract implements Listener {

    @EventHandler
    public void onPlateUseEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock().getType() == Material.GOLD_PLATE) {
                Player player = event.getPlayer();
                String timeFormatted = Main.getInstance().timer.getTimeFormatted(player);
                double time = Main.getInstance().timer.getTime(player);
                if (!timeFormatted.equalsIgnoreCase("0,000")) {
                    Main.getInstance().timer.stopTimer(player);
                    player.sendTitle("You scored a time of " + timeFormatted, "+10 coins!");
                    Main.getInstance().saveBestTime(player.getUniqueId(), Main.getInstance().modesWherePlayerIn.get(player), time);
                    Main.getInstance().scoreboardManager.updateTimer(player, timeFormatted, Main.getInstance().getConfig().getDouble("best-times." + player.getUniqueId() + "." + Main.getInstance().modesWherePlayerIn.get(player), 0.000));
                    player.teleport(Main.getInstance().getConfigManager().getSpawnLocation(Main.getInstance().modesWherePlayerIn.get(player), Main.getInstance().islandWherePlayerIn.get(player)));
                }
                List<Location> playerBlocks = Main.getInstance().placedBlocks.get(player.getUniqueId());
                if (playerBlocks != null) {
                    for (Location blockLocation : playerBlocks) {
                        if (blockLocation.getBlock().getType() == Material.SANDSTONE)
                            blockLocation.getBlock().setType(Material.AIR);
                    }
                    Main.getInstance().placedBlocks.remove(player.getUniqueId());
                }
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getItemMeta() != null && event.getItem().getItemMeta().getDisplayName().contains("Island Selector"))
                event.getPlayer().openInventory(new islandSelectorGui(event.getPlayer()).getInventory());
        }
    }
}
