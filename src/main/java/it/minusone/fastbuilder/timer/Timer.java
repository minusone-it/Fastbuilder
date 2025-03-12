package it.minusone.fastbuilder.timer;

import cf.pies.replay.api.Replay;
import cf.pies.replay.api.data.stream.ReplayOutputStream;
import it.minusone.fastbuilder.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static it.minusone.fastbuilder.Main.replays;
import static it.minusone.fastbuilder.Main.replaysFiles;

public class Timer {

    private final HashMap<UUID, Integer> playerTimers = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> runningTasks = new HashMap<>();

    public void startTimer(Player player) {
        UUID uuid = player.getUniqueId();
        Replay replay = new Replay(player.getLocation());
        replay.addPlayer(player);
        replay.addRecording();
        replay.start();

        replays.computeIfAbsent(uuid, k -> new java.util.ArrayList<>()).add(replay);

        if (runningTasks.containsKey(uuid)) {
            runningTasks.get(uuid).cancel();
            runningTasks.remove(uuid);
            playerTimers.remove(uuid);
        }

        playerTimers.put(uuid, 0);

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!playerTimers.containsKey(uuid)) {
                    cancel();
                    runningTasks.remove(uuid);
                    return;
                }

                playerTimers.put(uuid, playerTimers.get(uuid) + 1);

                double currentTime = getTime(player);
                double bestTime = Main.getInstance().getConfig().getDouble("best-times." + player.getUniqueId() + "." + Main.getInstance().modesWherePlayerIn.get(player), 0.000);
                Main.getInstance().scoreboardManager.updateTimer(player, new DecimalFormat("0.000").format(currentTime).replace(",", "."), bestTime);
            }
        };

        task.runTaskTimer(Main.getInstance(), 0L, 1L);
        runningTasks.put(uuid, task);
    }

    public void stopTimer(Player player) {
        UUID uuid = player.getUniqueId();
        playerTimers.remove(uuid);
        if (runningTasks.containsKey(uuid)) {
            runningTasks.get(uuid).cancel();
            runningTasks.remove(uuid);
        }

        List<Replay> playerReplays = replays.get(uuid);
        if (playerReplays == null || playerReplays.isEmpty()) return;

        Replay replay = playerReplays.get(playerReplays.size() - 1);
        replay.removeRecording();
        replay.end();

        File replayDir = new File(Main.getInstance().getDataFolder(), "replay");
        if (!replayDir.exists()) replayDir.mkdirs();

        File replayFile = new File(replayDir, uuid + "_" + System.currentTimeMillis() + ".replay");
        try {
            ReplayOutputStream replayOutputStream = new ReplayOutputStream(new FileOutputStream(replayFile));
            replayOutputStream.writeReplay(replay);
            replaysFiles.computeIfAbsent(uuid, k -> new java.util.ArrayList<>()).add(replayFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTimeFormatted(Player player) {
        return new DecimalFormat("0.000").format(getTime(player)).replace(",", ".");
    }

    public double getTime(Player player) {
        return (playerTimers.getOrDefault(player.getUniqueId(), 0) * 0.05) - 0.050;
    }

    public boolean isPlayerInTimer(Player player) {
        return runningTasks.containsKey(player.getUniqueId());
    }
}
