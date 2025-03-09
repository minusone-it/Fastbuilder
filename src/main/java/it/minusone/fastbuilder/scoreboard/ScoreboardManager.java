package it.minusone.fastbuilder.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {
    private final Map<Player, Scoreboard> scoreboards = new HashMap<>();

    public void createScoreboard(Player player, String title) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("fastbuilder", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(title);

        addScoreboardLine(objective, "§eTime: §b0.000", 1);
        addScoreboardLine(objective, "§eBest: §aN/A", 2);

        scoreboards.put(player, scoreboard);
        player.setScoreboard(scoreboard);
    }

    public void updateTimer(Player player, double currentTime, double bestTime, String mode) {
        setLine(player, "§eTime: §b" + new DecimalFormat("0.000").format(currentTime), 1);
        setLine(player, "§eBest: §a" + (bestTime > 0 ? new DecimalFormat("0.000").format(bestTime): "N/A"), 2);
    }

    private void addScoreboardLine(Objective objective, String text, int line) {
        Score score = objective.getScore(ChatColor.translateAlternateColorCodes('&', text));
        score.setScore(line);
    }

    public void addLine(Player player, String text, int line) {
        Scoreboard scoreboard = scoreboards.get(player);
        if (scoreboard == null) return;

        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective == null) return;

        addScoreboardLine(objective, text, line);
    }

    public void setLine(Player player, String text, int line) {
        removeLine(player, line);
        addLine(player, text, line);
    }

    public void removeLine(Player player, int line) {
        Scoreboard scoreboard = scoreboards.get(player);
        if (scoreboard == null) return;

        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective == null) return;

        for (String entry: scoreboard.getEntries()) {
            if (objective.getScore(entry).getScore() == line) {
                scoreboard.resetScores(entry);
                break;
            }
        }
    }

    public void removeScoreboard(Player player) {
        scoreboards.remove(player);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}