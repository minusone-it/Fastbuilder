package it.minusone.fastbuilder.command.tabcompleter;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class fastbuilderTabCompleter implements TabCompleter {
    private final ConfigManager configManager;

    public fastbuilderTabCompleter() {
        this.configManager = new ConfigManager(Main.getInstance());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if ("join".startsWith(args[0].toLowerCase())) completions.add("join");
            if (sender.hasPermission("fastbuilder.admin")) {
                if ("addmode".startsWith(args[0].toLowerCase())) completions.add("addmode");
                if ("removemode".startsWith(args[0].toLowerCase())) completions.add("removemode");
                if ("set".startsWith(args[0].toLowerCase())) completions.add("set");
                if ("addcoins".startsWith(args[0].toLowerCase())) completions.add("addcoins");
                if ("setcoins".startsWith(args[0].toLowerCase())) completions.add("setcoins");
                if ("reloadconfig".startsWith(args[0].toLowerCase())) completions.add("reloadconfig");
            }
        } else if (args.length == 2) {
            if (sender.hasPermission("fastbuilder.admin")) {
                if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("removemode") || args[0].equalsIgnoreCase("join")) {
                    completions.addAll(configManager.getModes());
                } else if (args[0].equalsIgnoreCase("setcoins") || args[0].equalsIgnoreCase("addcoins")) {
                    List<String> players = new ArrayList<>();
                    for (OfflinePlayer player : Bukkit.getOfflinePlayers())
                        players.add(player.getName());
                    for (Player player : Bukkit.getOnlinePlayers())
                        players.add(player.getName());
                    completions.addAll(players);
                }
            }
        }

        return completions;
    }
}
