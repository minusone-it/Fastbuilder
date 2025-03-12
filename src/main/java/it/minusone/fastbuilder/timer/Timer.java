package it.minusone.fastbuilder.timer;

import it.minusone.fastbuilder.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

public class Timer {

    private final HashMap<UUID, Integer> playerTimers = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> runningTasks = new HashMap<>();

    public void startTimer(Player player) {
        UUID uuid = player.getUniqueId();

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
                Main.getInstance().scoreboardManager.updateTopThree(player, Main.getInstance().modesWherePlayerIn.get(player));
            }
        };

        task.runTaskTimer(Main.getInstance(), 0L, 0L);
        runningTasks.put(uuid, task);
    }

    public void stopTimer(Player player) {
        UUID uuid = player.getUniqueId();
        playerTimers.remove(uuid);
        if (runningTasks.containsKey(uuid)) {
            runningTasks.get(uuid).cancel();
            runningTasks.remove(uuid);
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
