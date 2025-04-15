package it.minusone.fastbuilder.scoreboard;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.scoreboard.helpers.Scoreboard;
import it.minusone.fastbuilder.scoreboard.helpers.bukkit.BPlayerBoard;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ScoreboardManager {

    private final Main plugin;
    private final DecimalFormat timeFormat = new DecimalFormat("0.000");

    public ScoreboardManager(Main plugin) {
        this.plugin = plugin;
    }

    public void createScoreboard(Player player) {
        Scoreboard.instance().createBoard(player,
                ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("scoreboard.title", "&b&lFastBuilder")));
    }

    public void updateScoreboard(Player player, String currentTime, double pbTime) {
        BPlayerBoard board = Scoreboard.instance().getBoard(player);
        if (board == null) return;

        ConfigurationSection scoreboardCfg = plugin.getConfig().getConfigurationSection("scoreboard.lines");
        if (scoreboardCfg == null) return;

        Map<Integer, String> lines = new TreeMap<>((a, b) -> b - a);

        for (String lineNumber : scoreboardCfg.getKeys(false)) {
            int line = Integer.parseInt(lineNumber);
            String text = scoreboardCfg.getString(lineNumber, "");
            lines.put(line, text);
        }

        for (Map.Entry<Integer, String> entry : lines.entrySet()) {
            if (!entry.getValue().contains("%top")) {
                String lineText = replacePlaceholders(player, entry.getValue(), currentTime, pbTime, Main.getInstance().mapWherePlayerIn.get(player).split("-")[0]);
                board.set(lineText + getChar(entry.getKey()), entry.getKey());
            }
        }
    }

    public void updateScoreboard(Player player) {
        BPlayerBoard board = Scoreboard.instance().getBoard(player);
        if (board == null) return;

        ConfigurationSection scoreboardCfg = plugin.getConfig().getConfigurationSection("scoreboard.lines");
        if (scoreboardCfg == null) return;


        Map<Integer, String> lines = new TreeMap<>((a, b) -> b - a);

        for (String lineNumber : scoreboardCfg.getKeys(false)) {
            int line = Integer.parseInt(lineNumber);
            String text = scoreboardCfg.getString(lineNumber, "");
            lines.put(line, text);
        }

        for (Map.Entry<Integer, String> entry : lines.entrySet()) {
            if (!entry.getValue().contains("%time%") && !entry.getValue().contains("%pb%")) {
                String lineText = replacePlaceholders(player, entry.getValue(), Main.getInstance().getConfigManager().getPB(player.getUniqueId(), Main.getInstance().mapWherePlayerIn.get(player)), Main.getInstance().mapWherePlayerIn.get(player).split("-")[0]);
                board.set(lineText + getChar(entry.getKey()), entry.getKey());
            }
        }
    }


    private String replacePlaceholders(Player player, String text, String currentTime, double pbTime, String mode) {
        return ChatColor.translateAlternateColorCodes('&', text)
                .replace("%time%", currentTime)
                .replace("%pb%", pbTime > 0 ? timeFormat.format(pbTime).replace(",", ".") : "§7-.---")
                .replace("%top_1%", getTopPlayer(1, mode))
                .replace("%top_2%", getTopPlayer(2, mode))
                .replace("%top_3%", getTopPlayer(3, mode))
                .replace("%coins%", String.valueOf(plugin.getConfigManager().getCoinsOfPlayer(player)));
    }

    private String replacePlaceholders(Player player, String text, double pbTime, String mode) {
        return ChatColor.translateAlternateColorCodes('&', text)
                .replace("%pb%", pbTime > 0 ? timeFormat.format(pbTime).replace(",", ".") : "§7-.---")
                .replace("%top_1%", getTopPlayer(1, mode))
                .replace("%top_2%", getTopPlayer(2, mode))
                .replace("%top_3%", getTopPlayer(3, mode))
                .replace("%coins%", String.valueOf(plugin.getConfigManager().getCoinsOfPlayer(player)));
    }

    private String getTopPlayer(int position, String mode) {
        try {
            List<Main.Entry> topList = plugin.tops.get(mode);
            if (topList == null || topList.size() < position) {
                return "§7-.---";
            }
            Main.Entry entry = topList.get(position - 1);
            return "§7" + entry.player.getName() + " §8: §e" + timeFormat.format(entry.time).replace(",", ".");
        } catch (Exception e) {
            return "§7-.---";
        }
    }

    public void removeScoreboard(Player player) {
        BPlayerBoard board = Scoreboard.instance().getBoard(player);
        if (board != null) {
            board.delete();
        }
    }

    public String getChar(int line) {
        return Arrays.asList(
                "§0",
                "§1",
                "§2",
                "§3",
                "§4",
                "§5",
                "§6",
                "§7",
                "§8",
                "§9",
                "§a",
                "§b",
                "§c",
                "§d",
                "§e",
                "§f"
        ).get(line);
    }
}