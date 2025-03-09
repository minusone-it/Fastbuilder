package it.minusone.fastbuilder.command.tabcompleter;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.config.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

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
            if ("addmode".startsWith(args[0].toLowerCase())) completions.add("addmode");
            if ("removemode".startsWith(args[0].toLowerCase())) completions.add("removemode");
            if ("set".startsWith(args[0].toLowerCase())) completions.add("set");
            if ("join".startsWith(args[0].toLowerCase())) completions.add("join");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("removemode") || args[0].equalsIgnoreCase("join")) {
                completions.addAll(configManager.getModes());
            }
        }

        return completions;
    }
}
