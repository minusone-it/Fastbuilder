package it.minusone.fastbuilder.scoreboard;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.scoreboard.helpers.Scoreboard;
import it.minusone.fastbuilder.scoreboard.helpers.bukkit.BPlayerBoard;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardManager {

    public void createScoreboard(Player player) {
        String title = Main.getInstance().getConfig().getString("scoreboard.title", "&b&lFastBuilder");
        Scoreboard.instance().createBoard(player, title.replace("&", "§"));
    }

    public void updateTimer(Player player, String currentTime, double bestTime) {
        List<String> keys = new ArrayList<>(Main.getInstance().getConfig().getConfigurationSection("scoreboard.lines")
                .getKeys(false));

        for (String key : keys) {
            try {
                int line = Integer.parseInt(key);
                String text = Main.getInstance().getConfig().getString("scoreboard.lines." + key, "")
                        .replace("%time%", currentTime)
                        .replace("%pb%", bestTime > 0 ? new DecimalFormat("0.000").format(bestTime).replace(",", ".") : "§7-.---")
                        .replace("%coins%", String.valueOf(Main.getInstance().getConfigManager().getCoinsOfPlayer(player)));
                setLine(player, text, line);
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public void updateTopThree(Player player, String mode) {
        List<Main.Entry> topList = Main.getInstance().tops.getOrDefault(mode, new ArrayList<>());

        String top1 = !topList.isEmpty() ? formatTop(topList.get(0)) : "§7-.---";
        String top2 = topList.size() > 1 ? formatTop(topList.get(1)) : "§7-.---";
        String top3 = topList.size() > 2 ? formatTop(topList.get(2)) : "§7-.---";

        ConfigurationSection linesSection = Main.getInstance().getConfig().getConfigurationSection("scoreboard.lines");
        if (linesSection == null) return;

        for (String key : linesSection.getKeys(false)) {
            try {
                int line = Integer.parseInt(key);
                String text = linesSection.getString(key, "")
                        .replace("%top_1%", top1)
                        .replace("%top_2%", top2)
                        .replace("%top_3%", top3);
                setLine(player, text, line);
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private String formatTop(Main.Entry entry) {
        String time = new DecimalFormat("0.000").format(entry.time).replace(",", ".");
        return entry.player.getName() + " §7: §e" + time;
    }

    public void setLine(Player player, String text, int line) {
        BPlayerBoard board = Scoreboard.instance().getBoard(player);
        board.set(text.replace("&", "§"), line);
    }

    public void removeScoreboard(Player player) {
        BPlayerBoard board = Scoreboard.instance().getBoard(player);
        board.delete();
    }
}
