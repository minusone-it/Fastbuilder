package it.minusone.fastbuilder.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.gui.ModeChangerGUI;
import it.minusone.fastbuilder.npc.NPCManager;
import it.minusone.fastbuilder.utils.c;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class onPlayerClickEntity extends PacketAdapter {
    public onPlayerClickEntity() {
        super(Main.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketPlayInUseEntity packet = (PacketPlayInUseEntity) event.getPacket().getHandle();
        Player player = event.getPlayer();
        if (packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT ||
                packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
            Field field;
            int entityId;
            try {
                field = packet.getClass().getDeclaredField("a");
                field.setAccessible(true);
                entityId = (int) field.get(packet);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (NPCManager.getNPC(player) != null) {
                if (entityId == NPCManager.getNPC(player).getNpc().getId()
                        && !Main.currentPlayerReplay.containsKey(player.getUniqueId())) {
                    c.pagesOfModeChanger.put(player, 0);
                    player.openInventory(new ModeChangerGUI(0).getInventory());
                } else if (NPCManager.getReplayNPC(player) != null && NPCManager.getReplayNPC(player).entityPlayer != null) {
                    if (entityId == NPCManager.getReplayNPC(player).entityPlayer.getId()
                            && player.getItemInHand().getType() == Material.AIR) {
                        onPlayerSneak.spectateEntity(player, NPCManager.getReplayNPC(player).entityPlayer.getBukkitEntity());
                    }
                }
            }
        }
    }
    /*public static void injectPacketListener(Player player) {
        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();

        pipeline.addBefore("packet_handler", "npc_interact_" + player.getUniqueId(), new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                try {
                    if (msg instanceof PacketPlayInUseEntity) {
                        PacketPlayInUseEntity packet = (PacketPlayInUseEntity) msg;
                        if (packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT ||
                                packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                            Field field = packet.getClass().getDeclaredField("a");
                            field.setAccessible(true);
                            int entityId = (int) field.get(packet);

                            if (NPCManager.getNPC(player) != null) {
                                if (entityId == NPCManager.getNPC(player).getNpc().getId()
                                        && !Main.currentPlayerReplay.containsKey(player.getUniqueId())) {
                                    player.openInventory(new ModeChangerGUI().getInventory());
                                } else if (NPCManager.getReplayNPC(player) != null && NPCManager.getReplayNPC(player).entityPlayer != null) {
                                    if (entityId == NPCManager.getReplayNPC(player).entityPlayer.getId()
                                            && player.getItemInHand().getType() == Material.AIR) {
                                        onPlayerSneak.spectateEntity(player, NPCManager.getReplayNPC(player).entityPlayer.getBukkitEntity());
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
                super.channelRead(ctx, msg);
            }
            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                super.channelInactive(ctx);
                pipeline.remove("npc_interact_" + player.getUniqueId());
            }
        });
    }*/
}
