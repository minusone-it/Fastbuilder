package it.minusone.fastbuilder.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ResetItem extends ItemStack {
    public ResetItem() {
        super(Material.DIAMOND_PICKAXE);
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName("§3Reset Stats");
        this.setItemMeta(itemMeta);
    }

}
