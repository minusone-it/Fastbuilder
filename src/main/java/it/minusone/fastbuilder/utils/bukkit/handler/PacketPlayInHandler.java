package it.minusone.fastbuilder.utils.bukkit.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import it.minusone.fastbuilder.utils.bukkit.entity.Hologram;
import it.minusone.fastbuilder.utils.bukkit.entity.NPC;
import it.minusone.fastbuilder.utils.bukkit.events.entity.ClickEvent;
import it.minusone.fastbuilder.utils.bukkit.events.packet.PlayInEvent;
import it.minusone.fastbuilder.utils.utils.ReflectionUtils;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;

public class PacketPlayInHandler extends ChannelDuplexHandler {
    private final Player player;

    public PacketPlayInHandler(Player player) {
        this.player = player;
    }

    @Override // In Packet
    public void channelRead(ChannelHandlerContext channel, Object packet) {
        if (packet == null)
            return;

        try {
            if (((PlayInEvent) new PlayInEvent(player, (Packet<?>) packet).call()).isCancelled())
                return;
            super.channelRead(channel, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (packet instanceof PacketPlayInUseEntity) {
            int id = (int) ReflectionUtils.getField(packet, "a");
            for (NPC npc : NPC.all) {
                if (npc.entityPlayer.getId() == id && npc.getClickEvent() != null) {
                    npc.getClickEvent().accept(new ClickEvent(player, ((PacketPlayInUseEntity) packet).a()));
                    break;
                }
            }
            for (Hologram hologram : Hologram.all) {
                if (hologram.armorStand.getId() == id && hologram.getClickEvent() != null) {
                    hologram.getClickEvent().accept(new ClickEvent(player, ((PacketPlayInUseEntity) packet).a()));
                    break;
                }
            }
        }
    }
}
