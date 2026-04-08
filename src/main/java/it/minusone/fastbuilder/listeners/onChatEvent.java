package it.minusone.fastbuilder.listeners;

import it.minusone.fastbuilder.Main;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class onChatEvent implements Listener {
    public void onChatEvent(PlayerChatEvent event) {
        if (Main.getInstance().getConfigManager().messageContainsPlaceholder(event.getMessage()))
            event.setMessage(Main.getInstance().getConfigManager().getMessagePlaceholderReplaced(event));

    }
}
