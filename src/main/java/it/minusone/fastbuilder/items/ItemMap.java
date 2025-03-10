package it.minusone.fastbuilder.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;

public class ItemMap extends ItemStack {
    public ItemMap(String map, String player, Player skullOwner, boolean isOccupied) {
        super(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) this.getItemMeta();
        skullMeta.setDisplayName("§r§7Island " + map);
        skullMeta.setLore(Collections.singletonList(isOccupied ? "§r§cOccupied by " + player : "§r§2Not occupied!"));
        skullMeta.setOwner(skullOwner == null ? "MHF_Question" : skullOwner.getName());
        this.setItemMeta(skullMeta);
    }
}
