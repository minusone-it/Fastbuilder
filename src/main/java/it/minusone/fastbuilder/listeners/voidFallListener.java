package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class voidFallListener implements Listener {
    @EventHandler
    public void onVoidFall(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String mode = Main.getInstance().modesWherePlayerIn.get(player);
        if (player.getLocation().getY() < Main.getInstance().getConfigManager().getSpawnLocation(mode, )) {

            if (parkour != null) {
                Location respawnLoc = Start.getInstance().getLastCheckpoint(uuid, parkour);
                if (respawnLoc == null) {
                    respawnLoc = Start.getInstance().getModes().get(parkour);
                }
                player.teleport(respawnLoc);
            }
        }
    }
}
