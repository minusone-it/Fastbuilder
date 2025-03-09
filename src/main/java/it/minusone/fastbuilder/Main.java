package it.minusone.fastbuilder;

import it.minusone.fastbuilder.command.fastbuilder;
import it.minusone.fastbuilder.command.tabcompleter.fastbuilderTabCompleter;
import it.minusone.fastbuilder.config.ConfigManager;
import it.minusone.fastbuilder.listeners.onPlayerJoinQuit;
import it.minusone.fastbuilder.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public final class Main extends JavaPlugin {

    public String PREFIX;
    private static Main INSTANCE;
    public ScoreboardManager scoreboardManager;
    public HashMap<Player, String> modesWherePlayerIn;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.PREFIX = "§7[§3§lFastbuilder§r§7] §f";
        this.scoreboardManager = new ScoreboardManager();
        this.configManager = new ConfigManager(this);
        modesWherePlayerIn = new HashMap<>();
        this.loadListeners();
        this.loadCommands();
    }

    @Override
    public void onDisable() {
    }

    private void loadListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new onPlayerJoinQuit(), this);
    }

    private void loadCommands() {
        this.getCommand("fastbuilder").setExecutor(new fastbuilder());
        this.getCommand("fastbuilder").setTabCompleter(new fastbuilderTabCompleter());
    }

    public static Main getInstance() {
        return INSTANCE;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
