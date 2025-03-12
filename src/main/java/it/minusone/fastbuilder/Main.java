package it.minusone.fastbuilder;

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
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public final class Main extends JavaPlugin {

    public String PREFIX;
    public ScoreboardManager scoreboardManager;
    public HashMap<Player, String> modesWherePlayerIn;
    public HashMap<Player, String> islandWherePlayerIn;
    public HashMap<String, List<HashMap<Double, Player>>> tops;
    public HashMap<UUID, List<Location>> placedBlocks;
    public Timer timer;
    public ConfigManager configManager;
    private static Main INSTANCE;

    @Override
    public void onEnable() {
        this.PREFIX = getConfig().getString("prefix");
        this.getLogger().info(this.PREFIX + "Starting...");
        //TODO: LICENSE SYSTEM
        long startTime = System.currentTimeMillis();
        INSTANCE = this;
        this.timer = new Timer();
        this.scoreboardManager = new ScoreboardManager();
        this.configManager = new ConfigManager(this);
        this.modesWherePlayerIn = new HashMap<>();
        this.islandWherePlayerIn = new HashMap<>();
        this.placedBlocks = new HashMap<>();
        this.tops = new HashMap<>();
        this.loadListeners();
        this.loadCommands();
        for (Player p : Bukkit.getOnlinePlayers())
            this.scoreboardManager.createScoreboard(p, this.PREFIX);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (modesWherePlayerIn.containsKey(p))
                        scoreboardManager.updateTopThree(p, modesWherePlayerIn.get(p));
                }
            }
        }.runTaskTimer(this, 0L, 0L);
        this.getLogger().info(this.PREFIX + "Started plugin in " + (System.currentTimeMillis() - startTime) + "ms!");
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

    public void updateLeaderboard(Player player, String mode, double time) {
        List<HashMap<Double, Player>> topList = tops.getOrDefault(mode, new ArrayList<>());

        HashMap<Double, Player> previousEntry = null;
        double previousTime = Double.MAX_VALUE;

        for (HashMap<Double, Player> entry : topList) {
            for (Map.Entry<Double, Player> mapEntry : entry.entrySet()) {
                if (mapEntry.getValue().equals(player)) {
                    previousEntry = entry;
                    previousTime = mapEntry.getKey();
                    break;
                }
            }
            if (previousEntry != null) break;
        }

        if (previousEntry != null && time >= previousTime) return;

        if (previousEntry != null) {
            topList.remove(previousEntry);
        }

        HashMap<Double, Player> newEntry = new HashMap<>();
        newEntry.put(time, player);
        topList.add(newEntry);

        topList.sort(Comparator.comparingDouble(map -> map.keySet().iterator().next()));

        while (topList.size() > 10) {
            topList.remove(topList.size() - 1);
        }

        tops.put(mode, topList);
    }
    public void removePlayerFromLeaderboard(Player player, String mode) {
        List<HashMap<Double, Player>> topList = tops.get(mode);

        if (topList == null) return;

        Iterator<HashMap<Double, Player>> iterator = topList.iterator();
        while (iterator.hasNext()) {
            HashMap<Double, Player> entry = iterator.next();
            for (Map.Entry<Double, Player> mapEntry : entry.entrySet()) {
                if (mapEntry.getValue().equals(player)) {
                    iterator.remove();
                    break;
                }
            }
        }

        tops.put(mode, topList);
    }



}
