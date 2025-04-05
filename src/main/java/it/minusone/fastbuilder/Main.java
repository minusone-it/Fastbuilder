package it.minusone.fastbuilder;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import it.minusone.fastbuilder.command.fastbuilder;
import it.minusone.fastbuilder.command.stats;
import it.minusone.fastbuilder.command.tabcompleter.fastbuilderTabCompleter;
import it.minusone.fastbuilder.config.ConfigManager;
import it.minusone.fastbuilder.listeners.*;
import it.minusone.fastbuilder.replay.Replay;
import it.minusone.fastbuilder.replay.ReplayMain;
import it.minusone.fastbuilder.replay.ReplayPlayback;
import it.minusone.fastbuilder.scoreboard.ScoreboardManager;
import it.minusone.fastbuilder.timer.Timer;
import it.minusone.fastbuilder.utils.a;
import it.minusone.fastbuilder.utils.bukkit.entity.Hologram;
import it.minusone.fastbuilder.utils.bukkit.entity.HologramStack;
import it.minusone.fastbuilder.utils.bukkit.entity.NPC;
import it.minusone.fastbuilder.utils.bukkit.inventory.SignBuilder;
import it.minusone.fastbuilder.utils.bukkit.listeners.PlayerListener;
import it.minusone.fastbuilder.utils.minecraft.MinecraftConsole;
import it.minusone.fastbuilder.utils.utils.ReflectionUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public final class Main extends JavaPlugin {

    public static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
    public static final Map<UUID, ReplayPlayback> currentPlayerReplay = new HashMap<>();
    public static final List<Location> plateAboveLocations = new ArrayList<>();
    public static final Map<Material, Integer> blocks = new HashMap<>();
    private static Main INSTANCE;

    public static Main getInstance() {
        return INSTANCE;
    }

    public String PREFIX;
    public ProtocolManager protocolManager;
    public ConfigManager configManager;
    public ScoreboardManager scoreboardManager;
    public Timer timer;

    public final Map<Player, HologramStack> stats = new HashMap<>();
    public final Map<Player, String> mapWherePlayerIn = new HashMap<>();
    public final Map<UUID, Integer> page = new HashMap<>();
    public final Map<String, List<Entry>> tops = new HashMap<>();
    public final Map<UUID, List<Location>> placedBlocks = new HashMap<>();
    public final Map<UUID, Replay> replays = new HashMap<>();
    public final Map<UUID, Replay> lastReplay = new HashMap<>();

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();
        PREFIX = getConfig().getString("prefix");
        MinecraftConsole.printlnColor(PREFIX + "Starting...");

        long startTime = System.currentTimeMillis();
        configManager = new ConfigManager(this);

        if (!checkLicense()) return;

        if (Bukkit.getPluginManager().getPlugin("Fastbuilder").isEnabled()) {
            Bukkit.getOnlinePlayers().forEach(p ->
                    p.kickPlayer("Rejoin, please! Fastbuilder doesn't support reloads with players online."));

            initializePlugin();

            MinecraftConsole.printlnColor(PREFIX + "Took " + (System.currentTimeMillis() - startTime) + "ms to start the plugin!");
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (scoreboardManager != null) scoreboardManager.removeScoreboard(p);

            PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    try {
                        connection.sendPacket(new PacketPlayOutPlayerInfo(
                                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, (EntityPlayer) entity));
                        connection.sendPacket(new PacketPlayOutNamedEntitySpawn((EntityHuman) entity));
                    } catch (Exception ignored) {}
                }
            }
        });
    }

    private boolean checkLicense() {
        try (Socket socket = new Socket("minusone-services.ddns.net", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(getConfig().getString("license"));
            if (!Boolean.parseBoolean(in.readLine())) {
                MinecraftConsole.printlnColor(PREFIX + "§cLICENSE NOT VALID. PLEASE BUY A LICENSE ON [...]");
                Bukkit.getPluginManager().disablePlugin(this);
                return false;
            }

        } catch (IOException e) {
            MinecraftConsole.printlnColor(PREFIX + "License server error: " + e.getClass().getSimpleName());
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }

    private void initializePlugin() {
        this.timer = new Timer();
        this.scoreboardManager = new ScoreboardManager();
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        ReplayMain.getApi().initialize(this);
        initBlocks();
        loadListeners();
        loadCommands();
        startRepeatingTasks();

        if (configManager.isJumpPlateEnabled()) {
            Bukkit.getWorlds().forEach(a::scanLoadedChunksForPressurePlates);
        }
    }

    private void initBlocks() {
        blocks.put(Material.SANDSTONE, 0);
        blocks.put(Material.RED_SANDSTONE, 0);
        blocks.put(Material.WOOL, 100);
        blocks.put(Material.STONE, 100);
        blocks.put(Material.WOOD, 100);
        blocks.put(Material.QUARTZ_BLOCK, 200);
        blocks.put(Material.STAINED_CLAY, 300);
        blocks.put(Material.CLAY, 500);
        blocks.put(Material.HARD_CLAY, 500);
        blocks.put(Material.COBBLESTONE, 500);
        blocks.put(Material.COAL_BLOCK, 600);
        blocks.put(Material.SNOW_BLOCK, 600);
        blocks.put(Material.IRON_BLOCK, 700);
        blocks.put(Material.HAY_BLOCK, 700);
        blocks.put(Material.ENDER_STONE, 800);
        blocks.put(Material.NETHER_BRICK, 800);
        blocks.put(Material.LEAVES, 800);
        blocks.put(Material.STAINED_GLASS, 800);
        blocks.put(Material.MELON_BLOCK, 800);
        blocks.put(Material.PRISMARINE, 800);
        blocks.put(Material.REDSTONE_BLOCK, 900);
        blocks.put(Material.OBSIDIAN, 900);
        blocks.put(Material.EMERALD_BLOCK, 1000);
        blocks.put(Material.GOLD_BLOCK, 1000);
        blocks.put(Material.GOLD_ORE, 1000);
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new onPlayerJoinQuit(), this);
        Bukkit.getPluginManager().registerEvents(new onBlockPlaceBreak(), this);
        Bukkit.getPluginManager().registerEvents(new onPlayerMove(), this);
        Bukkit.getPluginManager().registerEvents(new onPlayerInteract(), this);
        Bukkit.getPluginManager().registerEvents(new onInventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new onEntityDamage(), this);
        Bukkit.getPluginManager().registerEvents(new onPlayerClickEntity(), this);
        Bukkit.getPluginManager().registerEvents(new onPlayerSneak(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        protocolManager.addPacketListener(new onPlayerReceivePacket());
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        ReflectionUtils.setStaticField(org.bukkit.enchantments.Enchantment.class, "acceptingNew", true);
    }

    private void loadCommands() {
        getCommand("fastbuilder").setExecutor(new fastbuilder());
        getCommand("fastbuilder").setTabCompleter(new fastbuilderTabCompleter());
        getCommand("stats").setExecutor(new stats());
    }

    private void startRepeatingTasks() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.setFoodLevel(20);
                    if (mapWherePlayerIn.containsKey(p)) {
                        scoreboardManager.updateTopThree(p, mapWherePlayerIn.get(p).split("-")[0]);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 0L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            try {
                for (NPC npc : NPC.all) {
                    if (npc.isGravity()) {
                        npc.entityPlayer.g(0, 0);
                        npc.updateNpcLocation();
                    }
                }

                for (Hologram h : Hologram.all) {
                    if (h.followEntity != null) {
                        h.setLocation(h.followEntity.getLocation().add(h.followOffset));
                        h.tracker.forEach(p -> p.sendPacket(new PacketPlayOutEntityTeleport(h.armorStand)));
                    }
                }

                Set<SignBuilder> toRemove = new HashSet<>();
                for (SignBuilder b : SignBuilder.signs) {
                    if (System.currentTimeMillis() - b.getGeneratedTime() > b.getTimeout()) {
                        toRemove.add(b);
                    }
                }
                SignBuilder.signs.removeAll(toRemove);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0L, 1L);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void saveBestTime(UUID playerId, String mode, double time) {
        String path = "best-times." + playerId + "." + mode;
        double currentBest = getConfig().getDouble(path, 0.0);
        if (currentBest == 0.0 || time < currentBest) {
            getConfig().set(path, time);
            saveConfig();
        }
    }

    public void updateLeaderboard(Player player, String mode, double time) {
        List<Entry> topList = tops.getOrDefault(mode, new ArrayList<>());
        Entry previousEntry = topList.stream()
                .filter(entry -> entry.player.equals(player))
                .findFirst()
                .orElse(null);

        if (previousEntry != null && time >= previousEntry.time) return;

        topList.remove(previousEntry);
        topList.add(new Entry(time, player));
        topList.sort(Comparator.comparingDouble(e -> e.time));

        if (topList.size() > 10) {
            topList.remove(topList.size() - 1);
        }

        tops.put(mode, topList);
    }

    public void removePlayerFromLeaderboard(Player player, String mode) {
        List<Entry> topList = tops.get(mode);
        if (topList != null && topList.removeIf(entry -> entry.player.equals(player))) {
            tops.put(mode, topList);
        }
    }

    public Player getPlayerInMode(String mode) {
        return mapWherePlayerIn.entrySet().stream()
                .filter(entry -> entry.getValue().equalsIgnoreCase(mode))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static class Entry {
        public double time;
        public Player player;

        public Entry(double time, Player player) {
            this.time = time;
            this.player = player;
        }
    }
}
