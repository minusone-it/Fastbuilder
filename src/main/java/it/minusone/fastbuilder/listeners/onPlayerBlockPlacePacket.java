package it.minusone.fastbuilder.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import it.minusone.fastbuilder.Main;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import org.bukkit.Location;
import org.bukkit.Material;

public class onPlayerBlockPlacePacket extends PacketAdapter {
    public onPlayerBlockPlacePacket() {
        super(Main.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_PLACE);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketPlayInBlockPlace packet = (PacketPlayInBlockPlace) event.getPacket().getHandle();

        if (packet.getFace() == 255) return;

        int x = packet.a().getX();
        int y = packet.a().getY();
        int z = packet.a().getZ();

        int face = packet.getFace();

        Location loc = new Location(event.getPlayer().getWorld(), x, y, z);

        switch (face) {
            case 0:
                loc.subtract(0, 1, 0);
                break;
            case 1:
                loc.add(0, 1, 0);
                break;
            case 2:
                loc.subtract(0, 0, 1);
                break;
            case 3:
                loc.add(0, 0, 1);
                break;
            case 4:
                loc.subtract(1, 0, 0);
                break;
            case 5:
                loc.add(1, 0, 0);
                break;
        }

        Material target = loc.getBlock().getType();
        Material targetA = new Location(event.getPlayer().getWorld(), x, y, z).getBlock().getType();
        if (target == Material.WATER || target == Material.STATIONARY_WATER || target == Material.LAVA || target == Material.STATIONARY_LAVA || targetA == Material.LONG_GRASS || targetA == Material.DOUBLE_PLANT || targetA == Material.VINE) {
            event.setCancelled(true);
            event.getPlayer().sendBlockChange(new Location(event.getPlayer().getWorld(), x, y, z).getBlock().getLocation(), new Location(event.getPlayer().getWorld(), x, y, z).getBlock().getType(), new Location(event.getPlayer().getWorld(), x, y, z).getBlock().getData());
            return;
        }

        if (Main.currentPlayerReplay.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
