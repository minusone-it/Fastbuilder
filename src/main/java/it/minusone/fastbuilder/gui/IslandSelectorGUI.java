package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.ItemMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IslandSelectorGUI implements InventoryHolder {
    private final Inventory inventory;
    private final Player requestedPlayer;

    public IslandSelectorGUI(Player requestedPlayer) {
        this.inventory = Bukkit.createInventory(this, 27, "Island Selector");
        this.requestedPlayer = requestedPlayer;
        this.addItems(this.inventory);
    }

    private void addItems(Inventory inventory) {
        String mode = Main.getInstance().modesWherePlayerIn.get(requestedPlayer);

        if (mode != null && Main.getInstance().getConfigManager().modeExists(mode)) {
            List<String> islands = Main.getInstance().getConfigManager().getIslandsOfMod(mode);

            Map<String, Player> islandToPlayerMap = Main.getInstance().islandWherePlayerIn.entrySet().stream()
                    .filter(entry -> islands.contains(entry.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

            for (String island : islands) {
                Player islandPlayer = islandToPlayerMap.get(island);
                boolean isOccupied = islandPlayer != null && Main.getInstance().modesWherePlayerIn.containsKey(islandPlayer);
                ItemMap mapItem = new ItemMap(island, islandPlayer != null ? islandPlayer.getName() : "", islandPlayer, isOccupied);
                inventory.addItem(mapItem);
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}