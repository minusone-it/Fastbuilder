package it.minusone.fastbuilder.config;

import it.minusone.fastbuilder.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConfigManager {
    private final Main plugin;
    private FileConfiguration config;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
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
            createModeWithDefaultNPCSpawn(mode);
            createModeWithDefaultHoloSpawn(mode);
        }
        plugin.saveConfig();
    }

    public List<String> getTimeMessage() {
        return config.getStringList("messages.time");
    }

    public List<String> getPersonalBestMessage() {
        return config.getStringList("messages.personal-best");
    }

    public void removeMode(String mode) {
        List<String> modes = getModes();
        if (modes.contains(mode)) {
            modes.remove(mode);
            setModes(modes);
        }
        plugin.saveConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public Location getSpawnLocation(String mode, String island) {
        String path = "spawns." + mode + "." + island;
        if (!config.contains(path)) return null;

        String worldName = config.getString(path + ".world");

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

    public int getCoinsOfPlayer(Player player) {
        String path = player.getUniqueId().toString();
        return config.getInt(path + ".coins");
    }

    public void addCoinsToPlayer(Player player, int coinsToAdd) {
        String path = player.getUniqueId().toString();
        coinsToAdd += getCoinsOfPlayer(player);
        config.set(path + ".coins", coinsToAdd);
        plugin.saveConfig();
    }

    public void setCoinsOfPlayer(Player player, int coins) {
        String path = player.getUniqueId().toString();
        if (config.get(path) == null)
            config.createSection(path);
        config.set(path + ".coins", coins);
        plugin.saveConfig();
    }

    public int getCoinsForResetStats() {
        return config.getInt("reset-stats-cost");
    }

    public boolean isJumpPlateEnabled() {
        return config.getBoolean("jump-plate");
    }

    public void addBlockToPlayer(ItemStack block, Player player) {
        String path = player.getUniqueId().toString() + ".blocks";
        List<String> ownedBlocks = config.getStringList(path);
        String blockData = block.getType().name() + ":" + block.getDurability();

        if (!ownedBlocks.contains(blockData)) {
            ownedBlocks.add(blockData);
            config.set(path, ownedBlocks);
            plugin.saveConfig();
        }
    }

    public boolean hasPlayerGotBlock(Player player, ItemStack block) {
        String path = player.getUniqueId().toString() + ".blocks";
        List<String> ownedBlocks = config.getStringList(path);
        String blockData = block.getType().name() + ":" + block.getDurability();
        return ownedBlocks.contains(blockData);
    }

    public ItemStack getCurrentPlayerBlock(Player player) {
        String path = player.getUniqueId().toString() + ".current-block";
        String blockData = config.getString(path, "WOOL:0");

        String[] parts = blockData.split(":");
        Material material = Material.matchMaterial(parts[0]);
        short durability = (parts.length > 1) ? Short.parseShort(parts[1]) : 0;

        if (material == null) {
            material = Material.WOOL;
        }

        return new ItemStack(material, 1, durability);
    }

    public void setCurrentPlayerBlock(Player player, ItemStack block) {
        String path = player.getUniqueId().toString() + ".current-block";
        String blockData = block.getType().name() + ":" + block.getDurability();
        config.set(path, blockData);
        plugin.saveConfig();
    }

    public Location getNPCOffset(String mode) {
        if (!config.contains("npcs." + mode + ".npc-offset")) return null;
        int x = config.getInt("npcs." + mode + ".npc-offset.x");
        int y = config.getInt("npcs." + mode + ".npc-offset.y");
        int z = config.getInt("npcs." + mode + ".npc-offset.z");
        float yaw = (float) config.getInt("modes." + mode + ".npc-offset.yaw");
        float pitch = (float) config.getInt("modes." + mode + ".npc-offset.pitch");
        return new Location(null, x, y, z, yaw, pitch);
    }

    public void createModeWithDefaultNPCSpawn(String mode) {
        String basePath = "npcs." + mode;

        if (config.contains(basePath))
            return;

        config.set(basePath + ".npc-offset.x", 1);
        config.set(basePath + ".npc-offset.y", 0);
        config.set(basePath + ".npc-offset.z", -1);
        config.set(basePath + ".npc-offset.yaw", 0);
        config.set(basePath + ".npc-offset.pitch", 0);

        plugin.saveConfig();
    }

    public void createModeWithDefaultHoloSpawn(String mode) {
        String basePath = "holos." + mode;

        if (config.contains(basePath))
            return;

        config.set(basePath + ".holo.x", -3);
        config.set(basePath + ".holo.y", 1);
        config.set(basePath + ".holo.z", 1);

        plugin.saveConfig();
    }

    public Location getHoloOffset(String mode, World world) {
        if (!config.contains("holos." + mode + ".holo")) return null;
        int x = config.getInt("holos." + mode + ".holo.x");
        int y = config.getInt("holos." + mode + ".holo.y");
        int z = config.getInt("holos." + mode + ".holo.z");
        return new Location(world, x, y, z, 0, 0);
    }

    public void addAttempt(Player player) {
        String path = player.getUniqueId().toString() + ".attempts";
        if (!config.contains(player.getUniqueId().toString())) return;

        config.set(path, config.getInt(path) + 1);
        plugin.saveConfig();
    }

    public void addSuccessfulAttempt(Player player) {
        String path = player.getUniqueId().toString() + ".successfulattempts";
        if (!config.contains(player.getUniqueId().toString())) return;

        config.set(path, config.getInt(path) + 1);
        plugin.saveConfig();
    }

    public long getAttempts(Player player) {
        String path = player.getUniqueId().toString() + ".attempts";
        return config.getLong(path);
    }

    public long getSuccessfulAttempts(Player player) {
        String path = player.getUniqueId().toString() + ".successfulattempts";
        return config.getLong(path);
    }

    public String getScoreTitle() {
        return config.getString("title");
    }

    public String getScoreSubTitle() {
        return config.getString("subtitle");
    }

    public String[] getHoloLines(Player player) {
        List<String> a = config.getStringList("holo");
        String[] c = new String[a.size()];
        for (int i = a.size(); i > 0; i--)
            c[i - 1] = a.get(i - 1).replace("%successful_attempts%", "" + getSuccessfulAttempts(player)).replace("%total_attempts%", "" + getAttempts(player)).replace("%pb%", new DecimalFormat("0.000").format(getPB(player.getUniqueId(), Main.getInstance().mapWherePlayerIn.get(player).split("-")[0])).replace(",", ".").equals("0.000") ? "-.---" : new DecimalFormat("0.000").format(getPB(player.getUniqueId(), Main.getInstance().mapWherePlayerIn.get(player).split("-")[0])).replace(",", ".")).replace("%player%", player.getName()).replace("&", "§");
        return c;
    }

    public boolean canPlaceBlocksOnWaterOrLava() {
        return config.getBoolean("blocks-on-water");
    }

    public boolean isLuckPermsSupportEnabled() {
        return Main.getInstance().luckpermsSupport && config.getBoolean("luckperms-support");
    }

    public String getNPCname() {
        return config.getString("npc-name");
    }

    public int getMaxDistance() {
        return config.getInt("max-distance");
    }

    public boolean isBungeeModeEnabled() {
        return config.getBoolean("ender-eye");
    }

    public String getLobbyItemName() {
        return config.getString("leave-item");
    }

    public String getSettingsItemName() {
        return config.getString("settings-item");
    }

    public String getIslandSelectorItemName() {
        return config.getString("islandselector-item");
    }

    public String getReplayItemName() {
        return config.getString("replay-item");
    }

}
