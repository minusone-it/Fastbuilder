package it.minusone.fastbuilder.utils.bukkit.events.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import it.minusone.fastbuilder.utils.bukkit.inventory.InventoryBuilder;

public class ClickInventoryEvent {

    private final Player player;
    private final InventoryClickEvent event;
    private final InventoryBuilder inventory;

    public ClickInventoryEvent(Player player, InventoryClickEvent event, InventoryBuilder inventory) {
        this.player = player;
        this.event = event;
        this.inventory = inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public InventoryClickEvent getEvent() {
        return event;
    }

    public InventoryBuilder getInventory() {
        return inventory;
    }
}