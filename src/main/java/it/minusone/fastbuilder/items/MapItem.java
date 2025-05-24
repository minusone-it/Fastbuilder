package it.minusone.fastbuilder.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapItem extends ItemStack {
    private static final Map<String, SkullMeta> skullMetaCache = new HashMap<>();

    public MapItem(String map, String player, Player skullOwner, boolean isOccupied) {
        super(Material.SKULL_ITEM, 1, (short) 3);

        if (!isOccupied) {
            this.setType(Material.STAINED_GLASS_PANE);
            this.setDurability((short) 14);
            ItemMeta meta = this.getItemMeta();
            meta.setLore(Collections.singletonList("§r§2Not occupied!"));
        } else {

            String ownerName = skullOwner != null ? skullOwner.getName() : "";

            SkullMeta skullMeta = getCachedSkullMeta(ownerName);

            skullMeta.setDisplayName("§r§7Island " + map);
            skullMeta.setLore(Collections.singletonList(
                    "§r§cOccupied by " + player
            ));

            this.setItemMeta(skullMeta);
        }
    }

    private static SkullMeta getCachedSkullMeta(String ownerName) {
        if (skullMetaCache.containsKey(ownerName)) {
            return skullMetaCache.get(ownerName).clone();
        }

        ItemStack dummySkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta newMeta = (SkullMeta) dummySkull.getItemMeta();
        newMeta.setOwner(ownerName);

        skullMetaCache.put(ownerName, newMeta);
        return newMeta.clone();
    }

}