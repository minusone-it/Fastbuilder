package it.minusone.fastbuilder.utils.bukkit.events.inventory;

import it.minusone.fastbuilder.utils.bukkit.inventory.InventoryBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class CloseInventoryEvent {

    private final Player player;

    private final InventoryCloseEvent event;

    private final InventoryBuilder inventory;

    public CloseInventoryEvent(Player player, InventoryCloseEvent event, InventoryBuilder inventory) {
        this.player = player;
        this.event = event;
        this.inventory = inventory;
    }

}