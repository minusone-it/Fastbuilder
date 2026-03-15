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
        String island = Main.getInstance().getIslandOfPlayer(player);
        String mode = Main.getInstance().getModeOfPlayer(player);
        try (ReplayInputStream replayInputStream = new ReplayInputStream(Files.newInputStream(replayFile.toPath()))) {
            Replay replay = replayInputStream.readReplay(Main.getInstance().getConfigManager().getSpawnLocation(mode, island));
            ReplayPlayback playback = new ReplayPlayback(Main.getInstance(), replay, Main.getInstance().getConfigManager().getSpawnLocation(mode, island));
            playback.addListener(player);
            playback.setup();
            Main.currentPlayerReplay.put(player.getUniqueId(), playback);
            player.getInventory().setItem(8, Main.getInstance().getConfigManager().getExitReplayItem());
            player.getInventory().setItem(4, Main.getInstance().getConfigManager().getStopReplayItem());
            player.getInventory().setItem(3, Main.getInstance().getConfigManager().getPrevTickReplayItem());
            player.getInventory().setItem(5, Main.getInstance().getConfigManager().getNextTickReplayItem());
        } catch (Exception e) {
            e.printStackTrace();
            Main.currentPlayerReplay.remove(player.getUniqueId());
            player.performCommand("fb join " + mode + " " + island);
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);
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
        ReplayPlayback playback = new ReplayPlayback(Main.getInstance(), replay, Main.getInstance().getConfigManager().getSpawnLocation(Main.getInstance().mapWherePlayerIn.get(player).split("-")[0], Main.getInstance().mapWherePlayerIn.get(player).split("-")[1]));
        playback.addListener(player);
        playback.setup();
        Main.currentPlayerReplay.put(player.getUniqueId(), playback);
        player.getInventory().setItem(8, Main.getInstance().getConfigManager().getExitReplayItem());
        player.getInventory().setItem(4, Main.getInstance().getConfigManager().getStopReplayItem());
        player.getInventory().setItem(3, Main.getInstance().getConfigManager().getPrevTickReplayItem());
        player.getInventory().setItem(5, Main.getInstance().getConfigManager().getNextTickReplayItem());
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
            return new DecimalFormat("0.000").format((replayInputStream.readReplay(new Location(Bukkit.getWorld("world"), 1, 1, 1)).getLength() / 20.0)).replace(",", ".");
        } catch (Exception ignored) {
        }
        return null;
    }

}