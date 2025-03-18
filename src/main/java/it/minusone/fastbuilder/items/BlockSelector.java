package it.minusone.fastbuilder.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockSelector extends ItemStack {
    public BlockSelector() {
        super(Material.SANDSTONE);
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "Block Selector");
        this.setItemMeta(itemMeta);
    }

}
