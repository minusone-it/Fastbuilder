package it.minusone.fastbuilder.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsItem extends ItemStack {
    public SettingsItem() {
        super(Material.EMERALD);
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName("§2Settings §7(Right-Click to use)");
        this.setItemMeta(itemMeta);
    }

}
