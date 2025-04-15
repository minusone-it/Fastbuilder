package it.minusone.fastbuilder.items;

import it.minusone.fastbuilder.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class PracticeModeItem extends ItemStack {
    public PracticeModeItem(Player incomingPlayer) {
        super(Material.STAINED_CLAY);
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Practice Mode");
        itemMeta.setLore(Collections.singletonList(Main.getInstance().playersInPracticeMode.get(incomingPlayer.getUniqueId()) ? "§r§2Enabled" : "§r§4Disabled"));
        this.setItemMeta(itemMeta);
    }

}
