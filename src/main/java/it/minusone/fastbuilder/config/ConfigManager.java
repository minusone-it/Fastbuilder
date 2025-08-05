package it.minusone.fastbuilder.config;

import it.minusone.fastbuilder.Main;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

public class ConfigManager {

    private final Main plugin;
    private final File playersFile;
    private final File arenasFile;
    private final File messagesFile;
    private final File guisFile;
    private final File itemsFile;
    private FileConfiguration config;
    private FileConfiguration playersConfig;
    private FileConfiguration arenasConfig;
    private FileConfiguration messagesConfig;
    private FileConfiguration guisConfig;
    private FileConfiguration itemsConfig;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        // default config
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();

        // custom files
        playersFile = new File(plugin.getDataFolder(), "players.yml");
        arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        guisFile = new File(plugin.getDataFolder(), "guis.yml");
        itemsFile = new File(plugin.getDataFolder(), "items.yml");

        if (!playersFile.exists()) plugin.saveResource("players.yml", false);
        if (!arenasFile.exists()) plugin.saveResource("arenas.yml", false);
        if (!messagesFile.exists()) plugin.saveResource("messages.yml", false);
        if (!guisFile.exists()) plugin.saveResource("guis.yml", false);
        if (!itemsFile.exists()) plugin.saveResource("items.yml", false);

        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        guisConfig = YamlConfiguration.loadConfiguration(guisFile);
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
    }

    // Save methods
    private void savePlayersConfig() {
        try {
            playersConfig.save(playersFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveArenasConfig() {
        try {
            arenasConfig.save(arenasFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadConfigs() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        guisConfig = YamlConfiguration.loadConfiguration(guisFile);
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
    }

    // -------- ARENAS (arenas.yml) --------

    public List<String> getModes() {
        return arenasConfig.getStringList("modes");
    }

    public void setModes(List<String> modes) {
        arenasConfig.set("modes", modes);
        saveArenasConfig();
    }

    public List<String> getIslandsOfMod(String mode) {
        List<String> spawns = new ArrayList<>();
        if (arenasConfig.contains("spawns." + mode))
            spawns.addAll(arenasConfig.getConfigurationSection("spawns." + mode).getKeys(false));
        return spawns;
    }

    public void addMode(String mode, boolean isInclined) {
        System.out.println(arenasConfig.saveToString());
        List<String> modes = getModes();
        List<String> inclined = arenasConfig.getStringList("inclined-modes");
        if (!modes.contains(mode)) {
            modes.add(mode);
            if (isInclined) inclined.add(mode);
            setModes(modes);
            arenasConfig.set("inclined-modes", inclined);
            createModeWithDefaultNPCSpawn(mode);
            createModeWithDefaultHoloSpawn(mode);
        }
        saveArenasConfig();
    }

    public void removeMode(String mode) {
        List<String> modes = getModes();
        if (modes.remove(mode)) setModes(modes);
        saveArenasConfig();
    }

    public boolean modeExists(String mode) {
        return getModes().contains(mode);
    }

    public boolean isModeInclined(String mode) {
        return arenasConfig.getStringList("inclined-modes").contains(mode);
    }

    public Location getSpawnLocation(String mode, String island) {
        String path = "spawns." + mode + "." + island;
        if (!arenasConfig.contains(path)) return null;
        String world = arenasConfig.getString(path + ".world");
        double x = arenasConfig.getDouble(path + ".x");
        double y = arenasConfig.getDouble(path + ".y");
        double z = arenasConfig.getDouble(path + ".z");
        float yaw = (float) arenasConfig.getDouble(path + ".yaw");
        float pitch = (float) arenasConfig.getDouble(path + ".pitch");
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public void setSpawnLocation(String mode, String island, Location loc) {
        String p = "spawns." + mode + "." + island;
        arenasConfig.set(p + ".world", loc.getWorld().getName());
        arenasConfig.set(p + ".x", loc.getX());
        arenasConfig.set(p + ".y", loc.getY());
        arenasConfig.set(p + ".z", loc.getZ());
        arenasConfig.set(p + ".yaw", loc.getYaw());
        arenasConfig.set(p + ".pitch", loc.getPitch());
        saveArenasConfig();
    }

    public Location getNPCOffset(String mode) {
        String base = "npcs." + mode + ".npc-offset";
        if (!arenasConfig.contains(base)) return null;
        double x = arenasConfig.getDouble(base + ".x");
        double y = arenasConfig.getDouble(base + ".y");
        double z = arenasConfig.getDouble(base + ".z");
        float yaw = (float) arenasConfig.getDouble(base + ".yaw");
        float pitch = (float) arenasConfig.getDouble(base + ".pitch");
        return new Location(null, x, y, z, yaw, pitch);
    }

    public void createModeWithDefaultNPCSpawn(String mode) {
        String b = "npcs." + mode + ".npc-offset";
        if (arenasConfig.contains(b)) return;
        arenasConfig.set(b + ".x", 1);
        arenasConfig.set(b + ".y", 0);
        arenasConfig.set(b + ".z", -1);
        arenasConfig.set(b + ".yaw", 0);
        arenasConfig.set(b + ".pitch", 0);
        saveArenasConfig();
    }

    public void createModeWithDefaultHoloSpawn(String mode) {
        String b = "holos." + mode + ".holo";
        if (arenasConfig.contains(b)) return;
        arenasConfig.set(b + ".x", -3);
        arenasConfig.set(b + ".y", 1);
        arenasConfig.set(b + ".z", 1);
        saveArenasConfig();
    }

    public Location getHoloOffset(String mode, World world) {
        String base = "holos." + mode + ".holo";
        if (!arenasConfig.contains(base)) return null;
        return new Location(world,
                arenasConfig.getDouble(base + ".x"),
                arenasConfig.getDouble(base + ".y"),
                arenasConfig.getDouble(base + ".z"),
                0, 0);
    }

    // -------- PLAYERS (players.yml) --------

    public void saveBestTime(UUID playerId, String mode, double time) {
        String path = "best-times." + playerId + "." + mode;
        double currentBest = playersConfig.getDouble(path, 0.0);
        if (currentBest == 0.0 || time < currentBest) {
            playersConfig.set(path, time);
            savePlayersConfig();
        }
    }

    public double getPB(UUID id, String mode) {
        return playersConfig.getDouble("best-times." + id + "." + mode, 0.0);
    }

    public int getCoinsOfPlayer(Player p) {
        return playersConfig.getInt(p.getUniqueId() + ".coins", 0);
    }

    public int getCoinsOfPlayer(OfflinePlayer p) {
        return playersConfig.getInt(p.getUniqueId() + ".coins", 0);
    }

    public void setCoinsOfPlayer(Player p, int coins) {
        String path = p.getUniqueId().toString();
        if (playersConfig.get(path) == null) playersConfig.createSection(path);
        playersConfig.set(path + ".coins", coins);
        savePlayersConfig();
    }

    public void setCoinsOfPlayer(OfflinePlayer p, int coins) {
        String path = p.getUniqueId().toString();
        if (playersConfig.get(path) == null) playersConfig.createSection(path);
        playersConfig.set(path + ".coins", coins);
        savePlayersConfig();
    }

    public void addCoinsToPlayer(Player p, int add) {
        int total = getCoinsOfPlayer(p) + add;
        playersConfig.set(p.getUniqueId() + ".coins", total);
        savePlayersConfig();
    }

    public void addCoinsToPlayer(OfflinePlayer p, int add) {
        int total = getCoinsOfPlayer(p) + add;
        playersConfig.set(p.getUniqueId() + ".coins", total);
        savePlayersConfig();
    }

    public void addBlockToPlayer(ItemStack block, Player p) {
        String path = p.getUniqueId() + ".blocks";
        List<String> owned = playersConfig.getStringList(path);
        String data = block.getType().name() + ":" + block.getDurability();
        if (!owned.contains(data)) {
            owned.add(data);
            playersConfig.set(path, owned);
            savePlayersConfig();
        }
    }

    public boolean hasPlayerGotBlock(Player p, ItemStack b) {
        String path = p.getUniqueId() + ".blocks";
        return playersConfig.getStringList(path).contains(b.getType().name() + ":" + b.getDurability());
    }

    public ItemStack getCurrentPlayerBlock(Player p) {
        String data = playersConfig.getString(p.getUniqueId() + ".current-block");
        String[] parts = data.split(":");
        Material m = Material.matchMaterial(parts[0]);
        short d = (parts.length > 1) ? Short.parseShort(parts[1]) : 0;
        return new ItemStack(m, 1, d);
    }

    public void setCurrentPlayerBlock(Player p, ItemStack b) {
        playersConfig.set(p.getUniqueId() + ".current-block",
                b.getType().name() + ":" + b.getDurability());
        savePlayersConfig();
    }

    public void addAttempt(Player p) {
        String path = p.getUniqueId() + ".attempts";
        if (!playersConfig.contains(p.getUniqueId().toString())) return;
        playersConfig.set(path, getAttempts(p) + 1);
        savePlayersConfig();
    }

    public void addSuccessfulAttempt(Player p) {
        String path = p.getUniqueId() + ".successfulattempts";
        if (!playersConfig.contains(p.getUniqueId().toString())) return;
        playersConfig.set(path, getSuccessfulAttempts(p) + 1);
        savePlayersConfig();
    }

    public long getAttempts(Player p) {
        return playersConfig.getLong(p.getUniqueId() + ".attempts", 0);
    }

    public long getSuccessfulAttempts(Player p) {
        return playersConfig.getLong(p.getUniqueId() + ".successfulattempts", 0);
    }

    public long getAttempts(OfflinePlayer p) {
        return playersConfig.getLong(p.getUniqueId() + ".attempts", 0);
    }

    public long getSuccessfulAttempts(OfflinePlayer p) {
        return playersConfig.getLong(p.getUniqueId() + ".successfulattempts", 0);
    }

    public void deleteStatsOfPlayer(OfflinePlayer player) {
        playersConfig.set("best-times." + player.getUniqueId().toString(), "");
        Main.getInstance().scoreboardManager.updateScoreboard(Bukkit.getPlayer(player.getName()), "0.000", 0.0);
        for (int i = 0; i < Main.getInstance().getConfigManager().getHoloLines(Bukkit.getPlayer(player.getName())).length; i++) {
            Main.getInstance().stats.get(Bukkit.getPlayer(player.getName())).setName(
                    i,
                    Main.getInstance().getConfigManager().getHoloLines(Bukkit.getPlayer(player.getName()))[i]
            );
        }
    }

    // -------- MESSAGES (messages.yml) --------

    public List<String> getTimeMessage() {
        return messagesConfig.getStringList("messages.time");
    }

    public List<String> getPersonalBestMessage() {
        return messagesConfig.getStringList("messages.personal-best");
    }

    public boolean isActionBarEnabled() {
        return !messagesConfig.getString("action-bar").isEmpty();
    }

    public String getActionBarMessage(Player p) {
        return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("action-bar").replace("%timer%", Main.getInstance().timer.getTimeFormatted(p)));
    }

    public String getScoreTitle() {
        return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("title"));
    }

    public String getScoreSubTitle() {
        return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("subtitle"));
    }

    // -------- GUIS CONFIG (guis.yml) --------

    public String getBlockSelectorGuiName() {
        return ChatColor.translateAlternateColorCodes('&', guisConfig.getString("block-selector.name"));
    }

    public int getBlockSelectorGuiMaxSlots() {
        return guisConfig.getInt("block-selector.max-slots");
    }

    public List<ItemStack> getItemStacksOfBlockSelectorGui() {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (guisConfig.getIntegerList(""))
    }

    public String getModeChangerGuiName() {
        return ChatColor.translateAlternateColorCodes('&', guisConfig.getString("mode-changer.name"));
    }

    public int getModeChangerGuiMaxSlots() {
        return guisConfig.getInt("mode-changer.max-slots");
    }

    public String getSettingsGuiName() {
        return ChatColor.translateAlternateColorCodes('&', guisConfig.getString("settings.name"));
    }

    public int getSettingsGuiMaxSlots() {
        return guisConfig.getInt("settings.max-slots");
    }

    public String getIslandSelectorGuiName() {
        return ChatColor.translateAlternateColorCodes('&', guisConfig.getString("island-selector"));
    }


    public String getConfirmGuiName() {
        return ChatColor.translateAlternateColorCodes('&', guisConfig.getString("confirm-stats-reset"));
    }

    public String getReplaysGuiName() {
        return ChatColor.translateAlternateColorCodes('&', guisConfig.getString("replays"));
    }

    // -------- ITEMS CONFIG (items.yml) --------

    public boolean isBungeeModeEnabled() {
        return itemsConfig.getBoolean("ender-eye");
    }

    public String getLobbyItemName() {
        return ChatColor.translateAlternateColorCodes('&', itemsConfig.getString("leave-item"));
    }

    public String getSettingsItemName() {
        return ChatColor.translateAlternateColorCodes('&', itemsConfig.getString("settings-item"));
    }

    public String getIslandSelectorItemName() {
        return ChatColor.translateAlternateColorCodes('&', itemsConfig.getString("islandselector-item"));
    }

    public String getReplayItemName() {
        return ChatColor.translateAlternateColorCodes('&', itemsConfig.getString("replay-item"));
    }

    public String getBackendServer() {
        return itemsConfig.getString("backend-server");
    }

    // -------- DEFAULT CONFIG (config.yml) --------

    public int getCoinsForResetStats() {
        return config.getInt("reset-stats-cost");
    }

    public boolean isJumpPlateEnabled() {
        return config.getBoolean("jump-plate");
    }

    public boolean isLuckPermsSupportEnabled() {
        return Main.getInstance().luckpermsSupport &&
                config.getBoolean("luckperms-support");
    }

    public int getMaxDistance() {
        return config.getInt("max-distance");
    }

    public String[] getHoloLines(Player p) {
        List<String> lines = config.getStringList("holo");
        String[] res = new String[lines.size()];
        String mode = Main.getInstance().mapWherePlayerIn.get(p).split("-")[0];
        String pb = new DecimalFormat("0.000").format(getPB(p.getUniqueId(), mode)).replace(",", ".");
        if (pb.equals("0.000")) pb = "-.---";
        for (int i = lines.size() - 1; i >= 0; i--) {
            res[i] = lines.get(i)
                    .replace("%successful_attempts%", String.valueOf(getSuccessfulAttempts(p)))
                    .replace("%total_attempts%", String.valueOf(getAttempts(p)))
                    .replace("%pb%", pb)
                    .replace("%player%", p.getName())
                    .replace("&", "§");
        }
        return res;
    }

    public String getNPCname() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("npc-name"));
    }

    public String getTopPlayerInModeByIndex(int idx, String mode) {
        return modeExists(mode)
                ? config.getStringList("wrs." + mode).get(idx)
                : null;
    }

}
