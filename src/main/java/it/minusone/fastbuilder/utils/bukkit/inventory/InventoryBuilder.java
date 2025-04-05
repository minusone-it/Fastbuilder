package it.minusone.fastbuilder.utils.bukkit.inventory;

import it.minusone.fastbuilder.utils.bukkit.events.inventory.ClickInventoryEvent;
import it.minusone.fastbuilder.utils.bukkit.events.inventory.CloseInventoryEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InventoryBuilder {
    public final Map<Integer, Consumer<ClickInventoryEvent>> clickEvents = new HashMap<>();
    public final boolean[] clickable;
    private final Player player;
    private final Inventory inventory;
    public Consumer<ClickInventoryEvent> allClickEvent;
    public Consumer<CloseInventoryEvent> closeInventoryEvent;

    //item destory at InventoryListener onClose -> iterator.remove();
    public InventoryBuilder(Player player, int size, String title) {
        inventory = Bukkit.createInventory(null, size, title);
        clickable = new boolean[size + 36];
        this.player = player;
        InventoryListener.inventoryMap.put(this, player);
    }

    public Inventory getInventory() {
        return inventory;
    }

    boolean isTempInventory() {
        return player != null;
    }

    public InventoryBuilder setItem(ItemStack stack, int slot) {
        inventory.setItem(slot, stack);
        return this;
    }

    public InventoryBuilder setItem(ItemStack stack, Consumer<ClickInventoryEvent> event, int slot) {
        inventory.setItem(slot, stack);
        clickEvents.put(slot, event);
        return this;
    }

    public InventoryBuilder setClickEvent(Consumer<ClickInventoryEvent> event, int slot) {
        clickEvents.put(slot, event);
        return this;
    }

    public InventoryBuilder setClickable(boolean flag, int slot) {
        clickable[slot] = flag;
        return this;
    }

}
