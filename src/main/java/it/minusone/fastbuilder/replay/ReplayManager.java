package it.minusone.fastbuilder.replay;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.replay.data.stream.ReplayInputStream;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.nio.file.Files;
import java.text.DecimalFormat;

public class ReplayManager {

    public static void playReplay(Player player, File replayFile) {
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        try (ReplayInputStream replayInputStream = new ReplayInputStream(Files.newInputStream(replayFile.toPath()))) {
            Replay replay = replayInputStream.readReplay(Main.getInstance().getConfigManager().getSpawnLocation(Main.getInstance().modesWherePlayerIn.get(player), Main.getInstance().islandWherePlayerIn.get(player)));
            ReplayPlayback playback = new ReplayPlayback(Main.getInstance(), replay, replay.getOrigin());
            playback.addListener(player);
            playback.setup();
            Main.currentPlayerReplay.put(player.getUniqueId(), playback);
            ItemStack oneTickBack = new ItemStack(Material.STICK, 1);
            ItemStack oneTickForward = new ItemStack(Material.BLAZE_ROD, 1);
            ItemStack cancel = new ItemStack(Material.BARRIER, 1);
            ItemStack start_stop = new ItemStack(Material.INK_SACK, 1, (short) 1);
            ItemMeta meta = start_stop.getItemMeta();
            meta.setDisplayName("§cStopped §7(Right-Click to use)");
            start_stop.setItemMeta(meta);
            ItemMeta meta1 = cancel.getItemMeta();
            meta1.setDisplayName("§cCancel §7(Right-Click to use)");
            cancel.setItemMeta(meta1);
            ItemMeta meta2 = oneTickBack.getItemMeta();
            ItemMeta meta3 = oneTickForward.getItemMeta();
            meta2.setDisplayName("§a-1 Tick §7(Right-Click to use)");
            meta3.setDisplayName("§a+1 Tick §7(Right-Click to use)");
            oneTickBack.setItemMeta(meta2);
            oneTickForward.setItemMeta(meta3);
            player.getInventory().setItem(8, cancel);
            player.getInventory().setItem(4, start_stop);
            player.getInventory().setItem(3, oneTickBack);
            player.getInventory().setItem(5, oneTickForward);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playReplay(Player player, Replay replay) {
        if (replay == null) {
            player.sendMessage(Main.getInstance().PREFIX + "§cNo last replay found!");
            return;
        }
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.getInventory().clear();
        ReplayPlayback playback = new ReplayPlayback(Main.getInstance(), replay, replay.getOrigin());
        playback.addListener(player);
        playback.setup();
        Main.currentPlayerReplay.put(player.getUniqueId(), playback);
        ItemStack oneTickBack = new ItemStack(Material.STICK, 1);
        ItemStack oneTickForward = new ItemStack(Material.BLAZE_ROD, 1);
        ItemStack cancel = new ItemStack(Material.BARRIER, 1);
        ItemStack start_stop = new ItemStack(Material.INK_SACK, 1, (short) 1);
        ItemMeta meta = start_stop.getItemMeta();
        meta.setDisplayName("§cStopped §7(Right-Click to use)");
        start_stop.setItemMeta(meta);
        ItemMeta meta1 = cancel.getItemMeta();
        meta1.setDisplayName("§cCancel §7(Right-Click to use)");
        cancel.setItemMeta(meta1);
        ItemMeta meta2 = oneTickBack.getItemMeta();
        ItemMeta meta3 = oneTickForward.getItemMeta();
        meta2.setDisplayName("§a-1 Tick §7(Right-Click to use)");
        meta3.setDisplayName("§a+1 Tick §7(Right-Click to use)");
        oneTickBack.setItemMeta(meta2);
        oneTickForward.setItemMeta(meta3);
        player.getInventory().setItem(8, cancel);
        player.getInventory().setItem(4, start_stop);
        player.getInventory().setItem(3, oneTickBack);
        player.getInventory().setItem(5, oneTickForward);
    }

    public static void runReplay(Player player) {
        try {
            ReplayPlayback playback = Main.currentPlayerReplay.get(player.getUniqueId());
            playback.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pauseReplay(Player player) {
        try {
            ReplayPlayback playback = Main.currentPlayerReplay.get(player.getUniqueId());
            playback.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void endReplay(Player player) {
        player.setAllowFlight(false);
        player.setGameMode(GameMode.SURVIVAL);
        try {
            ReplayPlayback playback = Main.currentPlayerReplay.get(player.getUniqueId());
            playback.end();
            Main.currentPlayerReplay.remove(player.getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDuration(File replayFile) {
        try (ReplayInputStream replayInputStream = new ReplayInputStream(Files.newInputStream(replayFile.toPath()))) {
            return new DecimalFormat("0.000").format(replayInputStream.readReplay(new Location(Bukkit.getWorld("world"), 1, 1, 1)).getLength() / 20.0).replace(",", ".");
        } catch (Exception ignored) {}
        return null;
    }

}