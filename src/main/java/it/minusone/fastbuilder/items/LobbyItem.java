package it.minusone.fastbuilder.items;

import it.minusone.fastbuilder.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LobbyItem extends ItemStack {
    public LobbyItem() {
        super(Main.getInstance().getConfigManager().getMaterialOfLobbyItem());
        this.setDurability(Main.getInstance().getConfigManager().getDurabilityOfLobbyItem());
        ItemMeta meta = this.getItemMeta();
        meta.setDisplayName(Main.getInstance().getConfigManager().getLobbyItemName());
        this.setItemMeta(meta);
    }
}
