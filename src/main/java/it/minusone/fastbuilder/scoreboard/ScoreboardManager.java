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
        objective.setDisplayName(title + " ");

        scoreboards.put(player, scoreboard);
        player.setScoreboard(scoreboard);
    }

    public void updateTimer(Player player, String currentTime, double bestTime) {
        setLine(player, "&a&8&l| " + ChatColor.GREEN, 8);
        setLine(player, "&a&8&l| §ePersonal Best:", 7);
        setLine(player, "&a&8&l| §a" + (bestTime > 0 ? new DecimalFormat("0.000").format(bestTime).replace(",", ".") : "-.---"), 6);
        setLine(player, "&a&8&l| " +  ChatColor.RESET, 5);
        setLine(player, "&a&8&l| §eTime:", 4);
        setLine(player, "&a&8&l| §b" + currentTime, 3);
        setLine(player, "&a&8&l| ", 2);
        setLine(player, "&a&8&l| &o- &6&o&8&l&o-", 1);
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