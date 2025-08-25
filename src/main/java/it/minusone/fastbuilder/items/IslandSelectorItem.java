package it.minusone.fastbuilder.items;

import it.minusone.fastbuilder.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class IslandSelectorItem extends ItemStack {
    public IslandSelectorItem() {
        super(Main.getInstance().getConfigManager().getMaterialOfIslandSelectorItem());
        this.setDurability(Main.getInstance().getConfigManager().getDurabilityOfIslandSelectorItem());
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(Main.getInstance().getConfigManager().getIslandSelectorItemName());
        this.setItemMeta(itemMeta);
    }
}
