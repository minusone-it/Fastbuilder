package it.minusone.fastbuilder.config;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.timer.Timer;
import it.minusone.fastbuilder.utils.b;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.haoshoku.nick.api.NickAPI;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        return messagesConfig.getStringList("end-messages.time");
    }

    public List<String> getPersonalBestMessage() {
        return messagesConfig.getStringList("end-messages.personal-best");
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

    public String getIslandOccupiedMessage(Player playerThatOccupies, String islandNumber) {
        if (Bukkit.getPluginManager().isPluginEnabled("NickAPI"))
            return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.island-already-occupied")).replace("%island_player%", NickAPI.getName(playerThatOccupies)).replace("%island_number%", islandNumber).replace("%prefix%", Main.getInstance().PREFIX);
        else
            return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.island-already-occupied")).replace("%island_player%", playerThatOccupies.getDisplayName()).replace("%island_number%", islandNumber).replace("%prefix%", Main.getInstance().PREFIX);
    }

    public String getUsageMessage(CommandType type) {
        String message = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.usage")).replace("%command%", "").replace("%prefix%", Main.getInstance().PREFIX);
        switch (type) {
            case JOIN:
                return message + "/fb join <mode>";
            case SET:
                return message + "/fb set <mode> <island>";
            case ADDMODE:
                return message + "/fb addmode <name> <isInclined(true | false)>";
            case ADDCOINS:
                return message + "/fb addcoins <player> <amount>";
            case SETCOINS:
                return message + "/fb setcoins <player> <amount>";
            case ADDISLAND:
                return message + "/fb addisland <mode> <isInclined>";
            case WIPESTATS:
                return message + "/fb wipestats <player>";
            case SEEREPLAY:
                return message + "/fb watchreplay <player> <mode> <id>";
            case REMOVEMODE:
                return message + "/fb removemode <mode>";
            case SEEREPLAYS:
                return message + "/fb seereplays <player> <mode> <page>";
            case SPECTATE:
                return message + "/spectate <player>";
            default:
                return "";
        }
    }

    public String getJoinMessage() {
        return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.joined-mode"));
    }

    public boolean isPlaceholdersEnabled() {
        return messagesConfig.getBoolean("placeholders.enabled");
    }

    public int getPlaceholdersSize() {
        int size = 1;
        ConfigurationSection section = messagesConfig.getConfigurationSection("placeholders");
        for (String key : section.getKeys(false)) {
            if (!(key.equalsIgnoreCase("enabled")))
                size++;
        }
        return size;
    }

    public String getPlaceholder(int idx) {
        ConfigurationSection section = messagesConfig.getConfigurationSection("placeholders");
        int i = 1;
        for (String key : section.getKeys(false)) {
            if (idx == i && !(key.equalsIgnoreCase("enabled")))
                return messagesConfig.getString("placeholders." + key + ".placeholder");
            i++;
        }
        return null;
    }

    public boolean messageContainsPlaceholder(String message) {
        for (int i = 1; i < getPlaceholdersSize(); i++) {
            if (message.contains(getPlaceholder(i)))
                return true;
        }
        return false;
    }

    public String getMessagePlaceholderReplaced(PlayerChatEvent event) {
        int i = 1;
        ConfigurationSection section = messagesConfig.getConfigurationSection("placeholders");
        for (String key : section.getKeys(false)) {
            if (!(key.equalsIgnoreCase("enabled")) && event.getMessage().contains(messagesConfig.getString("placeholders." + key + ".placeholder")))
                return event.getMessage().replace(messagesConfig.getString("placeholders." + key + ".placeholder"), getMessageConfigReplaced(i, event.getPlayer()));
            i++;
        }
        return null;
    }

    public String getMessageConfigReplaced(int idx, Player player) {
        int i = 1;
        ConfigurationSection section = messagesConfig.getConfigurationSection("placeholders");
        for (String key : section.getKeys(false)) {
            if (!(key.equalsIgnoreCase("enabled"))) {
                if (idx == i) {
                    return messagesConfig.getString("placeholders." + key + ".message")
                            .replace("%mode%", Main.getInstance().getModeOfPlayer(player))
                            .replace("%pb%", Timer.getFormat().format(Main.getInstance().getConfigManager().getPB(player.getUniqueId(), Main.getInstance().getModeOfPlayer(player))))
                            .replace("%coins%", String.valueOf(Main.getInstance().getConfigManager().getCoinsOfPlayer(player)));
                }
            }
            i++;
        }
        return null;
    }

    // -------- GUIS CONFIG (guis.yml) --------

    public String getBlockSelectorGuiName() {
        return ChatColor.translateAlternateColorCodes('&', guisConfig.getString("block-selector.name"));
    }

    public int getBlockSelectorGuiMaxSlots() {
        return guisConfig.getInt("block-selector.max-slots");
    }

    public List<ItemStack> getItemStacksOfSettingsGui(Player player) {
        ArrayList<ItemStack> list = new ArrayList<>();

        ConfigurationSection section = guisConfig.getConfigurationSection("settings-gui-slots");
        if (section != null) {
            for (int i = 0; i < getSettingsGuiMaxSlots() - 1; i++)
                list.add(new ItemStack(Material.AIR));
            for (String key : section.getKeys(false)) {
                int i = Integer.parseInt(key);
                String name = "§r" + guisConfig.getString("settings-gui-slots." + i + ".name").replace("&", "§");
                Material material = Material.getMaterial(guisConfig.getString("settings-gui-slots." + i + ".material").split(":")[0]);
                short data = Short.parseShort(guisConfig.getString("settings-gui-slots." + i + ".material").split(":")[1]);

                ItemStack item = new ItemStack(Material.AIR);
                if (material == null)
                    material = Material.getMaterial(Integer.parseInt(guisConfig.getString("settings-gui-slots." + i + ".material").split(":")[0]));
                if (material != null) {
                    item.setType(material);

                    item.setDurability(data);
                    List<String> configLore = guisConfig.getStringList("settings-gui-slots." + i + ".lore");
                    ItemMeta meta = getItemMeta(configLore, item, name, player);
                    item.setItemMeta(meta);
                }

                list.set(i - 1, item);
            }
        }
        return list;
    }

    private ItemMeta getItemMeta(List<String> configLore, ItemStack item, String name, Player player) {
        ArrayList<String> lore = new ArrayList<>();
        for (int idx = 0; idx < configLore.size(); idx++) {
            String s = configLore.get(idx);
            if (idx == 0 && s.trim().isEmpty()) {
                continue;
            }
            lore.add(s.replace("&", "§").replace("%price%", String.valueOf(getCoinsForResetStats())).replace("%practice_mode_enabled%", Main.getInstance().playersInPracticeMode.get(player.getUniqueId()) ? "§aEnabled" : "§cDisabled"));
        }
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(name);
        return meta;
    }

    public List<ItemStack> getItemStacksOfBlockSelectorGui(Player player, int page) {
        ArrayList<ItemStack> list = new ArrayList<>();

        ConfigurationSection section = guisConfig.getConfigurationSection("block-selector-slots." + page);
        if (section != null) {
            for (int i = 0; i < getBlockSelectorGuiMaxSlots() - 1; i++)
                list.add(new ItemStack(Material.AIR));
            for (String key : section.getKeys(false)) {
                int i = Integer.parseInt(key);
                String name = "§r" + guisConfig.getString("block-selector-slots." + page + "." + i + ".name").replace("&", "§");
                Material material = Material.getMaterial(guisConfig.getString("block-selector-slots." + page + "." + i + ".material").split(":")[0]);
                short data = Short.parseShort(guisConfig.getString("block-selector-slots." + page + "." + i + ".material").split(":")[1]);
                int price = guisConfig.getInt("block-selector-slots." + page + "." + i + ".price");

                ItemStack item = new ItemStack(Material.AIR);
                if (material == null)
                    material = Material.getMaterial(Integer.parseInt(guisConfig.getString("block-selector-slots." + page + "." + i + ".material").split(":")[0]));
                if (material != null) {
                    item.setType(material);

                    item.setDurability(data);
                    List<String> lore = new ArrayList<>();
                    lore.addAll(getLoreFormatted(
                            guisConfig.getStringList("block-selector-slots." + page + "." + i + ".lore"),
                            price, player, item
                    ));

                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(lore);
                    meta.setDisplayName(name);
                    item.setItemMeta(meta);
                }

                list.set(i - 1, item);
            }
        }
        return list;
    }

    public int getPriceOfBlock(Material block) {
        ConfigurationSection pagesSection = guisConfig.getConfigurationSection("block-selector-slots");
        if (pagesSection == null) return 0;

        for (String pageKey : pagesSection.getKeys(false)) {
            ConfigurationSection pageSection = pagesSection.getConfigurationSection(pageKey);
            if (pageSection == null) continue;

            for (String slotKey : pageSection.getKeys(false)) {
                ConfigurationSection blockSection = pageSection.getConfigurationSection(slotKey);
                if (blockSection == null) continue;

                String materialString = blockSection.getString("material");
                if (materialString == null) continue;

                String[] split = materialString.split(":");
                Material material = Material.getMaterial(split[0]);
                if (material == null) continue;

                if (material == block) {
                    return blockSection.getInt("price", 0);
                }
            }
        }

        return 0;
    }

    public int getMaxPageOfBlockSelectorGui() {
        return guisConfig.getConfigurationSection("block-selector-slots").getKeys(false).size();
    }

    public int getMaxPageOfModeChangerGui() {
        return guisConfig.getConfigurationSection("mode-changer-slots").getKeys(false).size();
    }

    public String getModeChangerGuiName() {
        return ChatColor.translateAlternateColorCodes('&', guisConfig.getString("mode-changer.name"));
    }

    public int getModeChangerGuiMaxSlots() {
        return guisConfig.getInt("mode-changer.max-slots");
    }

    public List<ItemStack> getItemStacksOfModeChangerGui(int page) {
        ArrayList<ItemStack> list = new ArrayList<>();

        ConfigurationSection section = guisConfig.getConfigurationSection("mode-changer-slots." + page);
        if (section != null) {
            for (int i = 0; i < getModeChangerGuiMaxSlots() - 1; i++)
                list.add(new ItemStack(Material.AIR));
            for (String key : section.getKeys(false)) {
                int i = Integer.parseInt(key);
                String name = "§r" + guisConfig.getString("mode-changer-slots." + page + "." + i + ".name").replace("&", "§");
                Material material = Material.getMaterial(guisConfig.getString("mode-changer-slots." + page + "." + i + ".material").split(":")[0]);
                short data = Short.parseShort(guisConfig.getString("mode-changer-slots." + page + "." + i + ".material").split(":")[1]);
                ItemStack item = new ItemStack(Material.AIR);
                if (material == null)
                    material = Material.getMaterial(Integer.parseInt(guisConfig.getString("mode-changer-slots." + page + "." + i + ".material").split(":")[0]));
                if (material != null) {
                    item.setType(material);

                    item.setDurability(data);
                    List<String> lore = new ArrayList<>();
                    lore.addAll(
                            getLoreOfModeChangerGui(page, i)
                    );

                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(lore);
                    meta.setDisplayName(name);
                    item.setItemMeta(meta);
                }

                list.set(i - 1, item);
            }
        }
        return list;
    }

    public ArrayList<String> getLoreOfModeChangerGui(int page, int i) {
        List<String> configLore = guisConfig.getStringList("mode-changer-slots." + page + "." + i + ".lore");
        ArrayList<String> lore = new ArrayList<>();
        for (int idx = 0; idx < configLore.size(); idx++) {
            String s = configLore.get(idx);
            if (idx == 0 && s.trim().isEmpty()) {
                continue;
            }
            lore.add(s.replace("&", "§"));
        }
        return lore;
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

    public boolean isLobbyToggled() {
        return itemsConfig.getBoolean("leave-item-toggled");
    }

    public boolean isBungeeToggled() {
        return itemsConfig.getBoolean("bungee");
    }

    public List<String> getCommandsToExecute() {
        return itemsConfig.getStringList("commands");
    }

    public String getLobbyItemName() {
        return ChatColor.translateAlternateColorCodes('&', itemsConfig.getString("leave-item"));
    }

    public Material getMaterialOfLobbyItem() {
        return Material.getMaterial(itemsConfig.getString("leave-item-material").split(":")[0]);
    }

    public short getDurabilityOfLobbyItem() {
        return Short.parseShort(itemsConfig.getString("leave-item-material").split(":")[1]);
    }

    public String getSettingsItemName() {
        return ChatColor.translateAlternateColorCodes('&', itemsConfig.getString("settings-item"));
    }

    public Material getMaterialOfSettingsItem() {
        return Material.getMaterial(itemsConfig.getString("settings-item-material").split(":")[0]);
    }

    public short getDurabilityOfSettingsItem() {
        return Short.parseShort(itemsConfig.getString("settings-item-material").split(":")[1]);
    }

    public String getIslandSelectorItemName() {
        return ChatColor.translateAlternateColorCodes('&', itemsConfig.getString("islandselector-item"));
    }

    public Material getMaterialOfIslandSelectorItem() {
        return Material.getMaterial(itemsConfig.getString("islandselector-item-material").split(":")[0]);
    }

    public short getDurabilityOfIslandSelectorItem() {
        return Short.parseShort(itemsConfig.getString("islandselector-item-material").split(":")[1]);
    }

    public String getReplayItemName() {
        return ChatColor.translateAlternateColorCodes('&', itemsConfig.getString("replay-item"));
    }

    public Material getMaterialOfReplayItem() {
        return Material.getMaterial(itemsConfig.getString("replay-item-material").split(":")[0]);
    }

    public short getDurabilityOfReplayItem() {
        return Short.parseShort(itemsConfig.getString("replay-item-material").split(":")[1]);
    }

    public String getBackendServer() {
        return itemsConfig.getString("backend-server");
    }

    public ItemStack nextPage() {
        ItemStack item = new ItemStack(Material.getMaterial(itemsConfig.getString("change-page.next-page.material").split(":")[0]));
        short durability = Short.parseShort(itemsConfig.getString("change-page.next-page.material").split(":")[1]);
        item.setDurability(durability);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemsConfig.getString("change-page.next-page.name").replace("&", "§"));
        List<String> configLore = itemsConfig.getStringList("change-page.next-page.lore");
        List<String> lore = new ArrayList<>();

        for (int i = 0; i < configLore.size(); i++) {
            String s = configLore.get(i);
            if (i == 0 && s.trim().isEmpty()) {
                continue;
            }
            lore.add(s.replace("&", "§"));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack previousPage() {
        ItemStack item = new ItemStack(Material.getMaterial(itemsConfig.getString("change-page.previous-page.material").split(":")[0]));
        short durability = Short.parseShort(itemsConfig.getString("change-page.previous-page.material").split(":")[1]);
        item.setDurability(durability);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemsConfig.getString("change-page.previous-page.name").replace("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> configLore = itemsConfig.getStringList("change-page.previous-page.lore");
        for (int i = 0; i < configLore.size(); i++) {
            String s = configLore.get(i);
            if (i == 0 && s.trim().isEmpty()) {
                continue;
            }
            lore.add(s.replace("&", "§"));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack getStopReplayItem() {
        ItemStack stopReplay = new ItemStack(Material.getMaterial(itemsConfig.getString("replays.stopped_replay.material").split(":")[0]));
        stopReplay.setDurability(Short.parseShort(itemsConfig.getString("replays.stopped_replay.material").split(":")[1]));
        ItemMeta meta = stopReplay.getItemMeta();
        meta.setDisplayName(itemsConfig.getString("replays.stopped_replay.name").replace("&", "§"));
        stopReplay.setItemMeta(meta);
        return stopReplay;
    }

    public ItemStack getStartReplayItem() {
        ItemStack stopReplay = new ItemStack(Material.getMaterial(itemsConfig.getString("replays.started_replay.material").split(":")[0]));
        stopReplay.setDurability(Short.parseShort(itemsConfig.getString("replays.started_replay.material").split(":")[1]));
        ItemMeta meta = stopReplay.getItemMeta();
        meta.setDisplayName(itemsConfig.getString("replays.started_replay.name").replace("&", "§"));
        stopReplay.setItemMeta(meta);
        return stopReplay;
    }

    public ItemStack getNextTickReplayItem() {
        ItemStack stopReplay = new ItemStack(Material.getMaterial(itemsConfig.getString("replays.next_tick.material").split(":")[0]));
        stopReplay.setDurability(Short.parseShort(itemsConfig.getString("replays.next_tick.material").split(":")[1]));
        ItemMeta meta = stopReplay.getItemMeta();
        meta.setDisplayName(itemsConfig.getString("replays.next_tick.name").replace("&", "§"));
        stopReplay.setItemMeta(meta);
        return stopReplay;
    }

    public ItemStack getPrevTickReplayItem() {
        ItemStack stopReplay = new ItemStack(Material.getMaterial(itemsConfig.getString("replays.prev_tick.material").split(":")[0]));
        stopReplay.setDurability(Short.parseShort(itemsConfig.getString("replays.prev_tick.material").split(":")[1]));
        ItemMeta meta = stopReplay.getItemMeta();
        meta.setDisplayName(itemsConfig.getString("replays.prev_tick.name").replace("&", "§"));
        stopReplay.setItemMeta(meta);
        return stopReplay;
    }

    public ItemStack getExitReplayItem() {
        ItemStack stopReplay = new ItemStack(Material.getMaterial(itemsConfig.getString("replays.cancel.material").split(":")[0]));
        stopReplay.setDurability(Short.parseShort(itemsConfig.getString("replays.cancel.material").split(":")[1]));
        ItemMeta meta = stopReplay.getItemMeta();
        meta.setDisplayName(itemsConfig.getString("replays.cancel.name").replace("&", "§"));
        stopReplay.setItemMeta(meta);
        return stopReplay;
    }

    public ItemStack bedItem() {
        ItemStack item = new ItemStack(Material.getMaterial(itemsConfig.getString("reset_item.material").split(":")[0]), 1, Short.parseShort(itemsConfig.getString("reset_item.material").split(":")[1]));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemsConfig.getString("reset_item.name").replace("&", "§"));
        item.setItemMeta(meta);
        return item;
    }

    public boolean isBedItemEnabled() {
        return itemsConfig.getBoolean("reset_item.enabled");
    }

    // -------- DEFAULT CONFIG (config.yml) --------

    public List<String> modesWithJumpPlate() {
       return config.getStringList("jump-plate-modes");
    }
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
            res[i] = b.parsePlaceholders(p, lines.get(i)
                    .replace("%successful_attempts%", String.valueOf(getSuccessfulAttempts(p)))
                    .replace("%total_attempts%", String.valueOf(getAttempts(p)))
                    .replace("%pb%", pb)
                    .replace("%player%", p.getName())
                    .replace("&", "§"));
        }
        return res;
    }

    public String getTopPlayerInModeByIndex(int idx, String mode) {
        return modeExists(mode)
                ? config.getStringList("wrs." + mode).get(idx)
                : null;
    }

    public String getNPCname() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("npc-name"));
    }

    public boolean npcsToggled() {
        return config.getBoolean("npcs");
    }

    public boolean holosToggled() {
        return config.getBoolean("holos");
    }

    // UTILS

    public List<String> getLoreFormatted(List<String> lore, int price, Player player, ItemStack item) {
        List<String> loreFormatted = new ArrayList<>();
        for (String s : lore) {
            loreFormatted.add(s.replace("%price%", "" + price).
                    replace("%block_status%", hasPlayerGotBlock(player, item) ? guisConfig.getString("block-status.purchased") : guisConfig.getString("block-status.not-purchased")).replace("&", "§"));
        }
        return loreFormatted;
    }

    public String modeOfItem(int idx, int page) {
        return guisConfig.getString("mode-changer-slots." + page + "." + idx + ".mode");
    }
    
    public String guiOfItem(int idx) {
        return guisConfig.getString("settings-gui-slots." + idx + ".gui");
    }
}
