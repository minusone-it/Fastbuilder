package it.minusone.fastbuilder.utils.bukkit.pluginMessage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import it.minusone.fastbuilder.utils.a;
import it.minusone.fastbuilder.utils.utils.bytes.ByteArrayBuffer;

public class BungeePluginMessage {

    public static void switchServer(Player player, String server) {
        sendPluginMessage(player, "BungeeCord", new ByteArrayBuffer().writeUTF("Connect").writeUTF(server).toByteArray());
    }

    public static void sendPluginMessage(Player player, String channel, byte[] out) {
        if (player == null)
            Bukkit.getServer().sendPluginMessage(a.getPlugin(a.class), channel, out);
        else
            player.sendPluginMessage(a.getPlugin(a.class), channel, out);
    }
}
