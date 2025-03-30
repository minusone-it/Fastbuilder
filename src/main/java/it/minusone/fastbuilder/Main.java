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
import it.minusone.fastbuilder.utils.bukkit.entity.Hologram;
import it.minusone.fastbuilder.utils.bukkit.entity.HologramStack;
import it.minusone.fastbuilder.utils.bukkit.entity.NPC;
import it.minusone.fastbuilder.utils.bukkit.inventory.SignBuilder;
import it.minusone.fastbuilder.utils.bukkit.listeners.PlayerListener;
import it.minusone.fastbuilder.utils.jasfafs;
import it.minusone.fastbuilder.utils.minecraft.MinecraftConsole;
import it.minusone.fastbuilder.utils.utils.ReflectionUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class Main extends JavaPlugin {

    public static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
    public static HashMap<UUID, ReplayPlayback> currentPlayerReplay;
    public static List<Location> plateAboveLocations;
    public static HashMap<Material, Integer> blocks;
    private static Main INSTANCE;
    public String PREFIX;
    public ScoreboardManager scoreboardManager;
    public HashMap<Player, HologramStack> stats;
    public HashMap<Player, String> modesWherePlayerIn;
    public HashMap<Player, String> islandWherePlayerIn;
    public HashMap<UUID, Integer> page;
    public Map<String, List<Entry>> tops;
    public HashMap<UUID, List<Location>> placedBlocks;
    public Timer timer;
    public ConfigManager configManager;
    public HashMap<UUID, Replay> replays;
    public HashMap<UUID, Replay> lastReplay;
    public ProtocolManager protocolManager;

    public static Main getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        this.PREFIX = getConfig().getString("prefix");
        MinecraftConsole.printlnColor(this.PREFIX + "Starting...");
        long startTime = System.currentTimeMillis();
        this.configManager = new ConfigManager(this);
        String licenseKey = this.getConfig().getString("license");
        boolean isValid;
        try (Socket socket = new Socket("minusone-services.ddns.net", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println(licenseKey);
            isValid = Boolean.parseBoolean(in.readLine());
            if (!isValid) {
                MinecraftConsole.printlnColor(this.PREFIX + "§cLICENSE NOT VALID. PLEASE BUY A LICENSE ON https://builtbybit.com/resources/fastbuilder.62493/ OR IF YOU BOUGHT PLUGIN GET A LICENSE ON https://discord.gg/CnQGtfEdjj.");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        } catch (IOException e) {
            MinecraftConsole.printlnColor(this.PREFIX +
                    "Error while trying to connect to the license server. Please contact support on discord with the error below.");
            MinecraftConsole.printlnColor(this.PREFIX +
                    "Error: " + e.getClass().getSimpleName() + ".");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        INSTANCE = this;
        if (Bukkit.getPluginManager().getPlugin("Fastbuilder").isEnabled()) {
            for (Player p : Bukkit.getOnlinePlayers())
                p.kickPlayer("Rejoin, please! Fastbuilder doesn't support reloads with players online.");
            this.timer = new Timer();
            this.scoreboardManager = new ScoreboardManager();
            this.modesWherePlayerIn = new HashMap<>();
            this.islandWherePlayerIn = new HashMap<>();
            this.placedBlocks = new HashMap<>();
            this.tops = new HashMap<>();
            this.lastReplay = new HashMap<>();
            this.replays = new HashMap<>();
            this.protocolManager = ProtocolLibrary.getProtocolManager();
            this.stats = new HashMap<>();
            this.page = new HashMap<>();
            plateAboveLocations = new ArrayList<>();
            ReplayMain.getApi().initialize(this);
            currentPlayerReplay = new HashMap<>();
            blocks = new HashMap<>();
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

            this.loadListeners();
            this.loadCommands();
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.setFoodLevel(20);
                        if (modesWherePlayerIn.containsKey(p))
                            scoreboardManager.updateTopThree(p, modesWherePlayerIn.get(p));
                    }
                }
            }.runTaskTimer(this, 0L, 0L);
            if (getConfigManager().isJumpPlateEnabled()) {
                for (World world : Bukkit.getServer().getWorlds())
                    jasfafs.scanLoadedChunksForPressurePlates(world);
            }
            MinecraftConsole.printlnColor(this.PREFIX + "Took " + (System.currentTimeMillis() - startTime) + "ms to start the plugin!");
        }
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (scoreboardManager != null)
                scoreboardManager.removeScoreboard(p);
            PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
            for (World world : Bukkit.getServer().getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    try {
                        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, (EntityPlayer) entity));
                        connection.sendPacket(new PacketPlayOutNamedEntitySpawn((EntityHuman) entity));
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    private void loadListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new onPlayerJoinQuit(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new onBlockPlaceBreak(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new onPlayerMove(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new onPlayerInteract(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new onInventoryClick(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new onEntityDamage(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new onPlayerClickEntity(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new onPlayerSneak(), this);
        this.protocolManager.addPacketListener(new onPlayerReceivePacket());
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        ReflectionUtils.setStaticField(org.bukkit.enchantments.Enchantment.class, "acceptingNew", true);
            /*try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Glow.glow = new Glow(100);
                Enchantment.registerEnchantment(Glow.glow);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            try {
                for (it.minusone.fastbuilder.utils.bukkit.entity.NPC n : NPC.all) {
                    if (n.isGravity()) {
                        n.entityPlayer.g(0, 0);
                        n.updateNpcLocation();
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
        }, 0, 1);

    }

    private void loadCommands() {
        this.getCommand("fastbuilder").setExecutor(new fastbuilder());
        this.getCommand("fastbuilder").setTabCompleter(new fastbuilderTabCompleter());
        this.getCommand("stats").setExecutor(new stats());
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
        List<Entry> topList = tops.getOrDefault(mode, new ArrayList<>());

        Entry previousEntry = null;

        for (Entry entry : topList) {
            if (entry.player.equals(player)) {
                previousEntry = entry;
                break;
            }
        }

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
        if (topList == null) return;

        boolean removed = topList.removeIf(entry -> entry.player.equals(player));

        if (removed) {
            tops.put(mode, topList);
        }
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
