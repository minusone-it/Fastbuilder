package it.minusone.fastbuilder.utils.bukkit.pluginMessage;

import it.minusone.fastbuilder.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import it.minusone.fastbuilder.utils.utils.bytes.ByteArrayBuffer;

public class BungeePluginMessage {

    public static void switchServer(Player player, String server) {
        sendPluginMessage(player, "BungeeCord", new ByteArrayBuffer().writeUTF("Connect").writeUTF(server).toByteArray());
    }

    public static void sendPluginMessage(Player player, String channel, byte[] out) {
        if (player == null)
            Bukkit.getServer().sendPluginMessage(Main.getPlugin(Main.class), channel, out);
        else
            player.sendPluginMessage(Main.getPlugin(Main.class), channel, out);
    }
}
