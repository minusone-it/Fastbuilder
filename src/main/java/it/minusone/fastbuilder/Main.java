package it.minusone.fastbuilder;

import it.minusone.fastbuilder.listeners.onPlayerJoinQuit;
import it.minusone.fastbuilder.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public String PREFIX;
    private static Main INSTANCE;
    public ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.PREFIX = "§7[§3§lFastbuilder§r§7] §f";
        this.scoreboardManager = new ScoreboardManager();
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

    }

    public static Main getInstance() {
        return INSTANCE;
    }
}
