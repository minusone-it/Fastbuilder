package it.minusone.fastbuilder.items;

import it.minusone.fastbuilder.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LobbyItem extends ItemStack {
    public LobbyItem() {
        super(Material.EYE_OF_ENDER);
        ItemMeta meta = this.getItemMeta();
        meta.setDisplayName(Main.getInstance().getConfigManager().getLobbyItemName());
        this.setItemMeta(meta);
    }
}
