package it.minusone.fastbuilder.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ModeItem extends ItemStack {
    public ModeItem(String mode) {
        super(Material.SANDSTONE);
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + mode);
        this.setItemMeta(itemMeta);
    }
}
