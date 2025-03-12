package it.minusone.fastbuilder.replay;

import cf.pies.replay.api.Replay;
import cf.pies.replay.api.ReplayPlayback;
import cf.pies.replay.api.data.stream.ReplayInputStream;
import java.io.File;
import java.io.FileInputStream;

import it.minusone.fastbuilder.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ReplayManager {
    public static void playReplay(Player player, File replayFile) {
        try (ReplayInputStream replayInputStream = new ReplayInputStream(new FileInputStream(replayFile))) {
            Replay replay = replayInputStream.readReplay(new Location(player.getWorld(), 102, 123, 168));
            ReplayPlayback playback = new ReplayPlayback(Main.getInstance(), replay, replay.getOrigin());
            playback.addListener(player);
            playback.setup();
            Main.currentPlayerReplay.put(player.getUniqueId(), playback);
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
    public static void runReplay(Player player) {
        try {
            ReplayPlayback playback = Main.currentPlayerReplay.get(player.getUniqueId());
            playback.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void endReplay(Player player) {
        try {
            ReplayPlayback playback = Main.currentPlayerReplay.get(player.getUniqueId());
            playback.end();
            Main.currentPlayerReplay.remove(player.getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
