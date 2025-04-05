package it.minusone.fastbuilder.utils.api.events;

import org.bukkit.entity.Player;

import java.util.List;

public class SignUpdateEvent {
    private final Player player;
    private final List<String> lines;

    public SignUpdateEvent(Player player, List<String> lines) {
        this.player = player;
        this.lines = lines;
    }

}
