package it.minusone.fastbuilder.items;

import it.minusone.fastbuilder.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ResetItem extends ItemStack {
    public ResetItem() {
        super(Material.DIAMOND_PICKAXE);
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName("§3Reset Stats");
        itemMeta.setLore(Arrays.asList(ChatColor.GRAY + "Reset your stats on all modes!", Main.getInstance().getConfigManager().getCoinsForResetStats() != 0 ? ChatColor.YELLOW + "Cost: " + Main.getInstance().getConfigManager().getCoinsForResetStats() + " Coins." : ChatColor.YELLOW + "Cost: §2No Cost!"));
        this.setItemMeta(itemMeta);
    }

}
