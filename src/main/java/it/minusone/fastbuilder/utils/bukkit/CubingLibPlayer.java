package it.minusone.fastbuilder.utils.bukkit;

import io.netty.channel.ChannelPipeline;
import it.minusone.fastbuilder.utils.bukkit.handler.PacketPlayInHandler;
import it.minusone.fastbuilder.utils.bukkit.handler.PacketPlayOutHandler;
import net.minecraft.server.v1_8_R3.EntityWither;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CubingLibPlayer {

    public static EntityWither sharedWither;
    public static Map<Player, CubingLibPlayer> user = new HashMap<>();

    static {
        EntityWither wither = new EntityWither(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle());
        wither.setInvisible(true);
        sharedWither = wither;
    }

    Player player;
    String bossbar;
    public CubingLibPlayer(Player player) {
        this.player = player;

        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();

        if (pipeline.get("decoder") != null)
            pipeline.addAfter("decoder", "speedcubingLib-packetplayin", new PacketPlayInHandler(player));
        if (pipeline.get("encoder") != null)
            pipeline.addAfter("encoder", "speedcubingLib-packetplayout", new PacketPlayOutHandler(player));

        user.put(player, this);
    }

    public static CubingLibPlayer get(Player player) {
        return user.get(player);
    }

    public String getBossbar() {
        return bossbar;
    }

}
