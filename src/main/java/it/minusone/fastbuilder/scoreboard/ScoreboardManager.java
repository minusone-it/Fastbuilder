package it.minusone.fastbuilder.scoreboard;

import it.minusone.fastbuilder.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        setLine(player, "&a&8&l| " + ChatColor.GREEN, 16);
        setLine(player, "&a&8&l| §e§lPersonal Best:    ", 15);
        setLine(player, "&a&8&l| §a" + (bestTime > 0 ? new DecimalFormat("0.000").format(bestTime).replace(",", ".") : "-.---"), 14);
        setLine(player, "&a&8&l| ", 13);
        setLine(player, "&a&8&l| §e§lSession-Top:" +  ChatColor.RESET, 12);
        setLine(player, "&a&8&l| §e§l" +  ChatColor.RESET, 8);
        setLine(player, "&a&8&l| §e§lCoins:", 7);
        setLine(player, "&a&8&l| §e" + Main.getInstance().getConfigManager().getCoinsOfPlayer(player), 6);
        setLine(player, "&a&8&l| " + ChatColor.BLACK, 5);
        setLine(player, "&a&8&l| §e§lTime:", 4);
        setLine(player, "&a&8&l| §b" + currentTime, 3);
        setLine(player, "&a&8&l| §b", 2);
        setLine(player, "&a&8&l| &6&o&7&o" + Main.getInstance().getConfigManager().getServerIP(), 1);
    }

    public void updateTopThree(Player player, String mode) {
        if (!Main.getInstance().tops.containsKey(mode)) {
            for (int i = 0; i < 3; i++) {
                setLine(player, "&a&8&l| §7-.---§" + i, 11 - i);
            }
        }

        List<HashMap<Double, Player>> topList = Main.getInstance().tops.get(mode);
        if (topList == null || topList.isEmpty()) return;

        // Ordinare i tempi migliori
        List<Map.Entry<Double, Player>> sortedTops = new ArrayList<>();
        for (HashMap<Double, Player> map : topList) {
            sortedTops.addAll(map.entrySet());
        }
        sortedTops.sort(Map.Entry.comparingByKey());

        // Aggiornare la scoreboard con i primi tre
        for (int i = 0; i < Math.min(3, sortedTops.size()); i++) {
            Map.Entry<Double, Player> entry = sortedTops.get(i);
            String formattedTime = new DecimalFormat("0.000").format(entry.getKey()).replace(",", ".");
            setLine(player, "&a&8&l| §6"  + entry.getValue().getName() + " §7- §b" + formattedTime, 11 - i);
        }
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