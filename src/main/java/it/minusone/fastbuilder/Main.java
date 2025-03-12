package it.minusone.fastbuilder;

import cf.pies.replay.api.Replay;

import cf.pies.replay.api.ReplayPlayback;
import it.minusone.fastbuilder.command.fastbuilder;
import it.minusone.fastbuilder.command.tabcompleter.fastbuilderTabCompleter;
import it.minusone.fastbuilder.config.ConfigManager;
import it.minusone.fastbuilder.listeners.*;
import it.minusone.fastbuilder.scoreboard.ScoreboardManager;
import it.minusone.fastbuilder.timer.Timer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {

    public static HashMap<UUID, List<Replay>> replays;
    public static HashMap<UUID, List<File>> replaysFiles;
    public static HashMap<UUID, ReplayPlayback> currentPlayerReplay;
    public String PREFIX;
    public ScoreboardManager scoreboardManager;
    public HashMap<Player, String> modesWherePlayerIn;
    public HashMap<Player, String> islandWherePlayerIn;
    public HashMap<UUID, List<Location>> placedBlocks;
    public Timer timer;
    public ConfigManager configManager;
    private static Main INSTANCE;

    @Override
    public void onEnable() {
        this.PREFIX = "§7[§3§lFastbuilder§r§7] §f";
        this.getLogger().info(this.PREFIX + "Starting...");
        INSTANCE = this;
        replays = new HashMap<>();
        replaysFiles = new HashMap<>();
        currentPlayerReplay = new HashMap<>();
        this.timer = new Timer();
        this.scoreboardManager = new ScoreboardManager();
        this.configManager = new ConfigManager(this);
        this.modesWherePlayerIn = new HashMap<>();
        this.islandWherePlayerIn = new HashMap<>();
        this.placedBlocks = new HashMap<>();
        this.loadListeners();
        this.loadCommands();
        for (Player p : Bukkit.getOnlinePlayers())
            this.scoreboardManager.createScoreboard(p, this.PREFIX);
        /*BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                onTickEvent.onTickEvent();
            }
        };
        task.runTaskTimer(this, 0L, 0L);*/
        this.getLogger().info(this.PREFIX + "Started plugin!");
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers())
            scoreboardManager.removeScoreboard(p);
    }

    private void loadListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new onPlayerJoinQuit(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new onBlockPlaceBreak(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new onPlayerMove(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new onPlayerInteract(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new onInventoryClickEvent(), this);
    }

    private void loadCommands() {
        this.getCommand("fastbuilder").setExecutor(new fastbuilder());
        this.getCommand("fastbuilder").setTabCompleter(new fastbuilderTabCompleter());
    }

    public static Main getInstance() {
        return INSTANCE;
    }

    public void saveBestTime(UUID playerId, String mode, double time) {
        String path = "best-times." + playerId + "." + mode;
        double currentBest = getConfig().getDouble(path, 0.0);

        if (currentBest == 0.0 || time < currentBest) {
            getConfig().set(path, time);
            saveConfig();
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
