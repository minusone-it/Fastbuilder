package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.items.ItemMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class islandSelectorGui implements InventoryHolder {
    private final Inventory inventory;
    private final Player requestedPlayer;
    public islandSelectorGui(Player requestedPlayer) {
        this.inventory = Bukkit.createInventory(this, 27, "Island Selector");
        this.requestedPlayer = requestedPlayer;
        this.addItems(this.inventory);
    }

    private void addItems(Inventory inventory) {
        List<ItemMap> maps = new ArrayList<>();

        String mode = Main.getInstance().modesWherePlayerIn.get(requestedPlayer);

        if (mode != null && Main.getInstance().getConfigManager().modeExists(mode)) {
            List<String> islands = Main.getInstance().getConfigManager().getIslandsOfMod(mode);

            for (String island : islands) {
                Player islandPlayer = null;

                for (Map.Entry<Player, String> entry : Main.getInstance().islandWherePlayerIn.entrySet()) {
                    if (entry.getValue().equals(island)) {
                        islandPlayer = entry.getKey();
                        break;
                    }
                }

                    boolean isOccupied = Main.getInstance().modesWherePlayerIn.containsKey(islandPlayer);

                maps.add(new ItemMap(island, islandPlayer != null ? islandPlayer.getName() : "", islandPlayer, isOccupied));

            }
        }

        for (ItemMap mapItem : maps) {
            inventory.addItem(mapItem);
        }
    }



    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
