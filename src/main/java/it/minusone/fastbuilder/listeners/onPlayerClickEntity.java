package it.minusone.fastbuilder.listeners;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import it.minusone.fastbuilder.gui.ModeChangerGUI;
import it.minusone.fastbuilder.npc.NPCManager;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;

public class onPlayerClickEntity implements Listener {

    public static void injectPacketListener(Player player) {
        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();

        pipeline.addBefore("packet_handler", "npc_interact_" + player.getUniqueId(), new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof PacketPlayInUseEntity) {
                    PacketPlayInUseEntity packet = (PacketPlayInUseEntity) msg;
                    if (packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT ||
                            packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                        Field field = packet.getClass().getDeclaredField("a");
                        field.setAccessible(true);
                        int entityId = (int) field.get(packet);
                        if (NPCManager.getNPC(player) != null) {
                            if (entityId == NPCManager.getNPC(player).getNpc().getId())
                                player.openInventory(new ModeChangerGUI().getInventory());
                            else {
                                if (NPCManager.getReplayNPC(player).entityPlayer != null) {
                                    if (entityId == NPCManager.getReplayNPC(player).entityPlayer.getId() && player.getItemInHand().getType() == Material.AIR)
                                        onPlayerSneak.spectateEntity(player, NPCManager.getReplayNPC(player).entityPlayer.getBukkitEntity());
                                }
                            }
                        }
                    }
                }
                super.channelRead(ctx, msg);
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                super.channelInactive(ctx);
                pipeline.remove("npc_interact_" + player.getUniqueId());
            }
        });
    }
}
