package it.minusone.fastbuilder.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PracticeModeItem extends ItemStack {
    public PracticeModeItem() {
        super(Material.STAINED_CLAY);
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Practice Mode");
        this.setItemMeta(itemMeta);
    }

}
