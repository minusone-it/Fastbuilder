package it.minusone.fastbuilder;

import it.minusone.fastbuilder.command.fastbuilder;
import it.minusone.fastbuilder.command.tabcompleter.fastbuilderTabCompleter;
import it.minusone.fastbuilder.config.ConfigManager;
import it.minusone.fastbuilder.listeners.onBlockPlace;
import it.minusone.fastbuilder.listeners.onPlateUse;
import it.minusone.fastbuilder.listeners.onPlayerJoinQuit;
import it.minusone.fastbuilder.listeners.voidFallListener;
import it.minusone.fastbuilder.scoreboard.ScoreboardManager;
import it.minusone.fastbuilder.timer.Timer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {

    public String PREFIX;
    private static Main INSTANCE;
    public ScoreboardManager scoreboardManager;
    public HashMap<Player, String> modesWherePlayerIn;
    public HashMap<Player, String> islandWherePlayerIn;
    public HashMap<UUID, List<Location>> placedBlocks;
    public Timer timer;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        this.getLogger().info(this.PREFIX + "Starting...");
        INSTANCE = this;
        this.PREFIX = "§7[§3§lFastbuilder§r§7] §f";
        this.timer = new Timer();
        this.scoreboardManager = new ScoreboardManager();
        this.configManager = new ConfigManager(this);
        this.modesWherePlayerIn = new HashMap<>();
        this.islandWherePlayerIn = new HashMap<>();
        this.placedBlocks = new HashMap<>();
        this.loadListeners();
        this.loadCommands();
        for (Player p : Bukkit.getOnlinePlayers()) {
p.addPotionEffect(PotionEffectType.SATURATION, 9999, 255);
} Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player player: Bukkit.getOnlinePlayers()) {
                String currentTime = timer.getTime(player);
                //double bestTime = getConfig().getDouble("best-times." + player.getUniqueId() + "." + modesWherePlayerIn.get(player), 0.0);
                scoreboardManager.updateTimer(player, currentTime, 0.000, modesWherePlayerIn.get(player));
            }
        }, 0L, 0L);
        this.getLogger().info(this.PREFIX + "Started plugin!");
    }

    @Override
    public void onDisable() {

    }

    private void loadListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new onPlayerJoinQuit(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new onBlockPlace(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new voidFallListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new onPlateUse(), this);
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
