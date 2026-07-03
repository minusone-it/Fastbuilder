package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class onChatEvent implements Listener {
    @EventHandler
    public void onChatEvent(PlayerChatEvent event) {
        System.out.println(event.getMessage());
        if (Main.getInstance().getConfigManager().messageContainsPlaceholder(event.getMessage()) && Main.getInstance().mapWherePlayerIn.containsKey(event.getPlayer()))
            event.setMessage(Main.getInstance().getConfigManager().getMessagePlaceholderReplaced(event));

    }
}
