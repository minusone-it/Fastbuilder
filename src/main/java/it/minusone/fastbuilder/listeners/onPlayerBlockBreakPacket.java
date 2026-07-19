package it.minusone.fastbuilder.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import it.minusone.fastbuilder.Main;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class onPlayerBlockBreakPacket extends PacketAdapter {
    public onPlayerBlockBreakPacket() {
        super(Main.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.BLOCK_DIG);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE || Main.getInstance().getIslandOfPlayer(event.getPlayer()) == null)
            return;
        PacketPlayInBlockDig packet = (PacketPlayInBlockDig) event.getPacket().getHandle();
        BlockPosition position = packet.a();
        Block block = event.getPlayer().getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());
        if (block.getType().equals(Material.BED_BLOCK) && Main.getInstance().getConfigManager().instaBedBreak()) {
            Main.getInstance().timer.scoreTime(event.getPlayer(), Main.getInstance().playersInPracticeMode.get(event.getPlayer().getUniqueId()));
        }
    }

}
