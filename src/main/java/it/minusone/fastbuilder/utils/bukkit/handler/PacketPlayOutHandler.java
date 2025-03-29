package it.minusone.fastbuilder.utils.bukkit.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;
import it.minusone.fastbuilder.utils.bukkit.events.packet.PlayOutEvent;

import java.util.List;

public class PacketPlayOutHandler extends MessageToMessageEncoder<Packet<?>> {

    private final Player player;

    public PacketPlayOutHandler(Player player) {
        this.player = player;
    }

    @Override // Out Packet
    protected void encode(ChannelHandlerContext channel, Packet<?> packet, List<Object> arg) {
        if (packet == null)
            return;
        try {
            if (((PlayOutEvent) new PlayOutEvent(player, packet).call()).isCancelled())
                return;
            arg.add(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}