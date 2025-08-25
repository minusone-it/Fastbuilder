package it.minusone.fastbuilder.listeners;

import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
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

    public static void resetCamera(Player player) {
        if (spectating.containsKey(player.getUniqueId())) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
                    new PacketPlayOutCamera(((CraftPlayer) player).getHandle())
            );
        }
    }

    @EventHandler
    public void onPlayerSneakEvent(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (event.isSneaking() && spectating.containsKey(player.getUniqueId())) {
            Entity entity = spectating.get(player.getUniqueId());
            Location entityLocation = entity.getLocation();
            player.teleport(entityLocation);
            resetCamera(player);

            spectating.remove(player.getUniqueId());
        }
    }

}
