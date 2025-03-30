package it.minusone.fastbuilder.utils.bukkit.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import it.minusone.fastbuilder.utils.api.events.SignUpdateEvent;
import it.minusone.fastbuilder.utils.bukkit.entity.Hologram;
import it.minusone.fastbuilder.utils.bukkit.entity.NPC;
import it.minusone.fastbuilder.utils.bukkit.events.entity.ClickEvent;
import it.minusone.fastbuilder.utils.bukkit.events.packet.PlayInEvent;
import it.minusone.fastbuilder.utils.bukkit.inventory.SignBuilder;
import it.minusone.fastbuilder.utils.utils.ReflectionUtils;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUpdateSign;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        } else if (packet instanceof PacketPlayInUpdateSign) {
            PacketPlayInUpdateSign p = (PacketPlayInUpdateSign) packet;
            Set<SignBuilder> toRemove = new HashSet<>();
            for (SignBuilder b : SignBuilder.signs) {
                if (b.getPlayer() == player) {
                    List<String> s = new ArrayList<>();
                    for (IChatBaseComponent i : p.b())
                        s.add(i.getText());
                    b.getEvent().accept(new SignUpdateEvent(player, s));
                }
                toRemove.add(b);
            }
            SignBuilder.signs.removeAll(toRemove);
        }
    }
}
