package it.minusone.fastbuilder.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import it.minusone.fastbuilder.Main;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;


public class onPlayerReceivePacket extends PacketAdapter {
    public onPlayerReceivePacket() {
        super(Main.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_PLACE);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketPlayInBlockPlace packet = (PacketPlayInBlockPlace) event.getPacket().getHandle();
        if (packet.getFace() == 255) return;
        if (Main.currentPlayerReplay.containsKey(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }
}
