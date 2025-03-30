package it.minusone.fastbuilder.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ItemMap extends ItemStack {
    // Cache per le SkullMeta già processate (owner -> SkullMeta clonabile)
    private static final Map<String, SkullMeta> skullMetaCache = new HashMap<>();

    public ItemMap(String map, String player, Player skullOwner, boolean isOccupied) {
        super(Material.SKULL_ITEM, 1, (short) 3);

        // Calcola l'owner della skull
        String ownerName = (skullOwner != null && isOccupied) ? skullOwner.getName() : "MHF_Question";

        // Ottieni o crea la SkullMeta dalla cache
        SkullMeta skullMeta = getCachedSkullMeta(ownerName);

        // Imposta nome e lore (dati unici per ogni ItemMap)
        skullMeta.setDisplayName("§r§7Island " + map);
        skullMeta.setLore(Collections.singletonList(
                isOccupied ? "§r§cOccupied by " + player : "§r§2Not occupied!"
        ));

        this.setItemMeta(skullMeta);
    }

    /**
     * Restituisce una SkullMeta dalla cache o ne crea una nuova.
     */
    private static SkullMeta getCachedSkullMeta(String ownerName) {
        // Se esiste già in cache, clonala per evitare conflitti
        if (skullMetaCache.containsKey(ownerName)) {
            return (SkullMeta) skullMetaCache.get(ownerName).clone();
        }

        // Altrimenti, crea una nuova SkullMeta e memorizzala in cache
        ItemStack dummySkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta newMeta = (SkullMeta) dummySkull.getItemMeta();
        newMeta.setOwner(ownerName); // Operazione lenta, viene fatta una sola volta per owner

        skullMetaCache.put(ownerName, newMeta);
        return (SkullMeta) newMeta.clone(); // Restituisci un clone per sicurezza
    }

}