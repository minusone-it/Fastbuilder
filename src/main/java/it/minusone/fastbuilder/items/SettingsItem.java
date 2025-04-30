package it.minusone.fastbuilder.items;

import it.minusone.fastbuilder.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsItem extends ItemStack {
    public SettingsItem() {
        super(Material.EMERALD);
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(Main.getInstance().getConfigManager().getSettingsItemName());
        this.setItemMeta(itemMeta);
    }

}
