package it.minusone.fastbuilder.items;

import it.minusone.fastbuilder.utils.a;
import it.minusone.fastbuilder.utils.b;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.Objects;

public class MapItem extends ItemStack {
    public MapItem(String map, String player, boolean isOccupied) {
        super(Material.STAINED_GLASS_PANE);
            ItemMeta meta = this.getItemMeta();
            meta.setDisplayName("§r§7Island " + map);
            if (isOccupied) {
                this.setDurability((short) 14);
                meta.setLore(Collections.singletonList(
                        "§r§cOccupied by " + player
                ));
            } else {
                this.setDurability((short) 13);
                meta.setLore(Collections.singletonList(
                        "§r§2Not occupied!"
                ));
            }
            this.setItemMeta(meta);
        }
}