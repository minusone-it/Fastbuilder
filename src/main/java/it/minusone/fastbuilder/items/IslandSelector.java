package it.minusone.fastbuilder.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class IslandSelector extends ItemStack {
    public IslandSelector() {
        super(Material.NETHER_STAR);
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName("§6Island Selector §7(Right-Click to use)");
        this.setItemMeta(itemMeta);
    }
}
