package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class onPlateUse implements Listener {

    @EventHandler
    public void onPlateUseEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock().getType() == Material.GOLD_PLATE) {
                Player player = event.getPlayer();
                String time = Main.getInstance().timer.getTime(player);
                if (!time.equalsIgnoreCase("0,000")) {
                    Main.getInstance().timer.stopTimer(player);
                    player.sendTitle("You scored a time of " + time, "+10 coins!");
                }
                List<Location> playerBlocks = Main.getInstance().placedBlocks.get(player.getUniqueId());
                if (playerBlocks != null) {
                    for (Location blockLocation : playerBlocks) {
                        if (blockLocation.getBlock().getType() == Material.SANDSTONE) {
                            blockLocation.getBlock().setType(Material.AIR);
                        }
                    }
                    Main.getInstance().placedBlocks.remove(player.getUniqueId());
                }
            }
        }
    }
}
