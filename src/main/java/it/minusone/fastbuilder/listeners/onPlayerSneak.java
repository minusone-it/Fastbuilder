package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.UUID;

public class onPlayerSneak implements Listener {

    private static final HashMap<UUID, Entity> spectating = new HashMap<>();

    public static void spectateEntity(Player player, Entity entity) {
        spectating.put(player.getUniqueId(), entity);

        PacketPlayOutCamera packet = new PacketPlayOutCamera(((CraftPlayer) entity).getHandle());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @EventHandler
    public void onPlayerSneakEvent(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (player.isSneaking() && spectating.containsKey(player.getUniqueId())) {
            Entity entity = spectating.get(player.getUniqueId());
            Location entityLocation = entity.getLocation();

            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                player.teleport(entityLocation);
                resetCamera(player);
            }, 0L);

            spectating.remove(player.getUniqueId());
        }
    }

    private void resetCamera(Player player) {
        PacketPlayOutCamera reset = new PacketPlayOutCamera(((CraftPlayer) player).getHandle());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(reset);
    }
}
