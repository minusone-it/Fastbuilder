package it.minusone.fastbuilder.scoreboard;

import it.minusone.fastbuilder.scoreboard.helpers.Scoreboard;
import it.minusone.fastbuilder.scoreboard.helpers.bukkit.BPlayerBoard;
import it.minusone.fastbuilder.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardManager {

    public void createScoreboard(Player player, String title) {
        Scoreboard.instance().createBoard(player, title);
    }

    public void updateTimer(Player player, String currentTime, double bestTime) {
        setLine(player, "&a&8&l| " + ChatColor.GREEN, 15);
        setLine(player, "&a&8&l| §b§lPersonal Best:    ", 14);
        setLine(player, "&a&8&l| §e" + (bestTime > 0
                ? new DecimalFormat("0.000").format(bestTime).replace(",", ".")
                : "§7-.---"), 13);
        setLine(player, "&a&8&l| ", 12);
        setLine(player, "&a&8&l| §e§lSession-Top:" + ChatColor.RESET, 11);
        setLine(player, "&a&8&l| §e§l" + ChatColor.RESET, 7);
        setLine(player, "&a&8&l| §e§lCoins:", 6);
        setLine(player, "&a&8&l| §e" + Main.getInstance().getConfigManager().getCoinsOfPlayer(player), 5);
        setLine(player, "&a&8&l| " + ChatColor.BLACK, 4);
        setLine(player, "&a&8&l| §a§lTime:", 3);
        setLine(player, "&a&8&l| §e" + currentTime, 2);
        setLine(player, "&a&8&l|   &7&o" + Main.getInstance().getConfigManager().getServerIP(), 1);
    }

    public void updateTopThree(Player player, String mode) {
        try {
            List<Main.Entry> topList = Main.getInstance().tops.get(mode);
            if (topList == null || topList.isEmpty()) {
                for (int i = 0; i < 3; i++)
                    setLine(player, "&a&8&l| §7-.---§" + i, 10 - i);
                return;
            }

            for (int i = 0; i < 3; i++) {
                if (i < topList.size()) {
                    Main.Entry entry = topList.get(i);
                    String formattedTime = new DecimalFormat("0.000").format(entry.time).replace(",", ".");
                    setLine(player, "&a&8&l| §7" + entry.player.getName() + " §7: §e" + formattedTime, 10 - i);
                } else {
                    setLine(player, "&a&8&l| §7-.---§" + i, 10 - i);
                }
            }
        } catch (Exception ignored) {

        }
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
