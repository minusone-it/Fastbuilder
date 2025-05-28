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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapItem extends ItemStack {
    public MapItem(String map, String player, Player skullOwner, boolean isOccupied) {
        super(Objects.requireNonNull(b.itemWithBase64(b.createSkull(), a.getPlayerSkinBase64((skullOwner != null && isOccupied) ? skullOwner.getName() : "MHF_Question"))));

        if (Bukkit.getOnlineMode()) {
            SkullMeta skullMeta = (SkullMeta) this.getItemMeta();

            skullMeta.setDisplayName("§r§7Island " + map);
            skullMeta.setLore(Collections.singletonList(
                    isOccupied ? "§r§cOccupied by " + player : "§r§2Not occupied!"
            ));
            this.setItemMeta(skullMeta);
        } else {
            this.setType(Material.STAINED_GLASS_PANE);
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
}