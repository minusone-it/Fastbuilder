package it.minusone.fastbuilder.config;

import it.minusone.fastbuilder.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {
    private final Main plugin;
    private final FileConfiguration config;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public List<String> getModes() {
        return config.getStringList("modes");
    }

    public void addMode(String mode) {
        List<String> modes = getModes();
        if (!modes.contains(mode)) {
            modes.add(mode);
            config.set("modes", modes);
            plugin.saveConfig();
        }
    }

    public void removeMode(String mode) {
        List<String> modes = getModes();
        if (modes.contains(mode)) {
            modes.remove(mode);
            config.set("modes", modes);
            plugin.saveConfig();
        }
    }

    public Location getSpawnLocation(String mode, String island) {
        String path = "spawns." + mode + "." + island;
        if (!config.contains(path)) return null;

        String worldName = config.getString(path + ".world");
        if (worldName == null) return null;

        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");

        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public boolean modeExists(String mode) {
        return !getModes().contains(mode);
    }
}
