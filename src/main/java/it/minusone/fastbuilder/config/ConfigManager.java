package it.minusone.fastbuilder.config;

import it.minusone.fastbuilder.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public void setModes(List<String> modes) {
        config.set("modes", modes);
        plugin.saveConfig();
    }

    public List<String> getIslandsOfMod(String mode) {
        List<String> spawns = new ArrayList<>();
        if (config.contains("spawns." + mode))
            spawns.addAll(config.getConfigurationSection("spawns." + mode).getKeys(false));
        return spawns;
    }

    public void addMode(String mode) {
        List<String> modes = getModes();
        if (!modes.contains(mode)) {
            modes.add(mode);
            setModes(modes);
        }
    }

    public void removeMode(String mode) {
        List<String> modes = getModes();
        if (modes.contains(mode)) {
            modes.remove(mode);
            setModes(modes);
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

    public void setSpawnLocation(String mode, String island, Location location) {
        String path = "spawns." + mode + "." + island;
        config.set(path + ".world", location.getWorld().getName());
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        config.set(path + ".yaw", location.getYaw());
        config.set(path + ".pitch", location.getPitch());
        plugin.saveConfig();
    }

    public boolean modeExists(String mode) {
        return getModes().contains(mode);
    }

    public double getPB(UUID playerId, String mode) {
        return config.getDouble("best-times." + playerId + "." + mode, 0.0);
    }

    public void setPB(UUID playerId, String mode, double time) {
        String path = "best-times." + playerId + "." + mode;
        double currentBest = getPB(playerId, mode);
        if (currentBest == 0.0 || time < currentBest) {
            config.set(path, time);
            plugin.saveConfig();
        }
    }

    public String getServerIP() {
        return config.getString("server-ip");
    }
}
