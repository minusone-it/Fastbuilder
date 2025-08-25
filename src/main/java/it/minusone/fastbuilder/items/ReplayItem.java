package it.minusone.fastbuilder.items;

import it.minusone.fastbuilder.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ReplayItem extends ItemStack {
    public ReplayItem() {
        super(Main.getInstance().getConfigManager().getMaterialOfReplayItem());
        this.setDurability(Main.getInstance().getConfigManager().getDurabilityOfReplayItem());
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(Main.getInstance().getConfigManager().getReplayItemName());
        this.setItemMeta(itemMeta);
    }
}
