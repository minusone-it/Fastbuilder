package it.minusone.fastbuilder.items;

import it.minusone.fastbuilder.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class BlockItem extends ItemStack {
    public BlockItem(Material block, Player player, int cost) {
        super(block);
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "" + block);
        itemMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Cost: " + cost + " coins", Main.getInstance().getConfigManager().hasPlayerGotBlock(player, new ItemStack(block)) ? ChatColor.GREEN + "You already own this block!" : ChatColor.RED + "You don't own this block yet!"));
        this.setItemMeta(itemMeta);
    }

    public BlockItem(Material block, Player player, int cost, int color) {
        super(block, 1, (short) color);
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "" + block);
        itemMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Cost: " + cost + " coins", Main.getInstance().getConfigManager().hasPlayerGotBlock(player, this) ? ChatColor.GREEN + "You already own this block!" : ChatColor.RED + "You don't own this block yet!"));
        this.setItemMeta(itemMeta);
    }

}
