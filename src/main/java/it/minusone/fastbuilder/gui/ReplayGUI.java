package it.minusone.fastbuilder.gui;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.replay.ReplayManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReplayGUI implements InventoryHolder {

    private static final int[] AVAILABLE_SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
    private static final int ITEMS_PER_PAGE = AVAILABLE_SLOTS.length;
    private final Inventory inventory;
    private final int page;

    public ReplayGUI(UUID playerUUID, int page) {
        File replayFolder = new File(Main.getPlugin(Main.class).getDataFolder(),
                "replays" + File.separator + playerUUID.toString() + File.separator +
                        Main.getInstance().mapWherePlayerIn.get(Bukkit.getPlayer(playerUUID)).split("-")[0]);

        if (!replayFolder.exists()) replayFolder.mkdirs();

        List<File> replayFiles = new ArrayList<>(Arrays.asList(Objects.requireNonNull(replayFolder.listFiles())));
        replayFiles.sort(Comparator.comparingLong(File::lastModified).reversed());

        int maxPage = replayFiles.isEmpty() ? 1 : (int) Math.ceil((double) replayFiles.size() / ITEMS_PER_PAGE);

        this.inventory = Bukkit.createInventory(
                this,
                54,
                Main.getInstance().getConfigManager().getReplaysGuiName()
                        .replace("%page%", String.valueOf(page + 1))
                        .replace("%max_page%", String.valueOf(maxPage))
        );
        this.page = page;

        addItems(replayFiles);
        Main.getInstance().page.put(playerUUID, page);
    }


    public static String unixToDate(long unixTime) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTime), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static void handleNavigation(Player player, ItemStack clickedItem, UUID playerUUID, int currentPage) {
        if (clickedItem.getType() == Material.ARROW) {
            if (clickedItem.getItemMeta().getDisplayName().contains(Main.getInstance().getConfigManager().previousPage().getItemMeta().getDisplayName())) {
                player.openInventory(new ReplayGUI(playerUUID, currentPage - 1).getInventory());
            } else if (clickedItem.getItemMeta().getDisplayName().contains(Main.getInstance().getConfigManager().nextPage().getItemMeta().getDisplayName())) {
                player.openInventory(new ReplayGUI(playerUUID, currentPage + 1).getInventory());
            }
        }
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void addItems(List<File> replayFiles) {
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, replayFiles.size());
        if (this.page == 0)
            inventory.setItem(0, createItem(Material.PAPER, "Last Replay"));
        for (int i = startIndex, slot = 0; i < endIndex; i++, slot++) {
            File replayFile = replayFiles.get(i);
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String replayName = replayFile.getName().replace(".replay", "");
                meta.setDisplayName("Replay: " + replayName);
                List<String> lore = new ArrayList<>();
                lore.add("§r§7Duration: " + ReplayManager.getDuration(replayFile));
                lore.add("§r§7Created: " + unixToDate(Long.parseLong(replayName) / 1000));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inventory.setItem(AVAILABLE_SLOTS[slot], item);
        }

        if (page > 0) {
            inventory.setItem(45, Main.getInstance().getConfigManager().previousPage());
        }

        if (replayFiles.size() > (page + 1) * ITEMS_PER_PAGE) {
            inventory.setItem(53, Main.getInstance().getConfigManager().nextPage());
        }
    }
}
