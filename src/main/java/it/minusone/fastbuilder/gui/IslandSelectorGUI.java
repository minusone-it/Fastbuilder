package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.MapItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.List;

public class IslandSelectorGUI implements InventoryHolder {
    private final Inventory inventory;
    private final Player requestedPlayer;

    public IslandSelectorGUI(Player requestedPlayer) {
        this.inventory = Bukkit.createInventory(this, 27, "Island Selector");
        this.requestedPlayer = requestedPlayer;
        this.addItems(this.inventory);
    }

    private void addItems(Inventory inventory) {
        String mode = Main.getInstance().mapWherePlayerIn.get(requestedPlayer).split("-")[0];

        if (mode != null && Main.getInstance().getConfigManager().modeExists(mode)) {
            List<String> islands = Main.getInstance().getConfigManager().getIslandsOfMod(mode);
            for (String island : islands) {
                Player islandPlayer = Main.getInstance().getPlayerInMode(mode + "-" + island);
                boolean isOccupied = islandPlayer != null;
                MapItem mapItem;
                if (!Bukkit.getServer().getPluginManager().isPluginEnabled("NickAPI"))
                    mapItem = new MapItem(island, isOccupied ? islandPlayer.getName() : "", islandPlayer, isOccupied);
                else
                    mapItem = new MapItem(island, isOccupied ?  NickAPI.getName(islandPlayer) : "", islandPlayer, isOccupied);
                inventory.addItem(mapItem);
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}