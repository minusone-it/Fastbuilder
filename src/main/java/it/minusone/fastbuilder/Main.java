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
import it.minusone.fastbuilder.utils.bukkit.listeners.PlayerListener;
import it.minusone.fastbuilder.utils.utils.ReflectionUtils;
import net.luckperms.api.LuckPerms;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.fusesource.jansi.Ansi;

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
    public static final Map<UUID, ReplayPlayback> currentPlayerReplay = new HashMap<>();
    public static final List<Location> plateAboveLocations = new ArrayList<>();
    public static final Map<Material, Integer> blocks = new HashMap<>();
    private static Main INSTANCE;
    public final Map<Player, HologramStack> stats = new HashMap<>();
    public final Map<Player, String> mapWherePlayerIn = new HashMap<>();
    public final Map<UUID, Integer> page = new HashMap<>();
    public final Map<String, List<Entry>> tops = new HashMap<>();
    public final Map<UUID, List<Location>> placedBlocks = new HashMap<>();
    public final Map<UUID, List<Location>> placedPracticeBlocks = new HashMap<>();
    public final Map<UUID, Replay> replays = new HashMap<>();
    public final Map<UUID, Replay> lastReplay = new HashMap<>();
    public final Map<UUID, Boolean> playersInPracticeMode = new HashMap<>();
    public String PREFIX;
    public ProtocolManager protocolManager;
    public ConfigManager configManager;
    public ScoreboardManager scoreboardManager;
    public Timer timer;
    public boolean luckpermsSupport;
    public LuckPerms luckpermsApi;
    private String newVersion;

    public static Main getInstance() {
        return INSTANCE;
    }

    private void printlnColor(String message) {
        Ansi reset = Ansi.ansi().a(Ansi.Attribute.RESET);
        System.out.println(message
                .replace("§0", reset.fg(Ansi.Color.BLACK).boldOff().toString())
                .replace("§1", reset.fg(Ansi.Color.BLUE).boldOff().toString())
                .replace("§2", reset.fg(Ansi.Color.GREEN).boldOff().toString())
                .replace("§3", reset.fg(Ansi.Color.CYAN).boldOff().toString())
                .replace("§4", reset.fg(Ansi.Color.RED).boldOff().toString())
                .replace("§5", reset.fg(Ansi.Color.MAGENTA).boldOff().toString())
                .replace("§6", reset.fg(Ansi.Color.YELLOW).boldOff().toString())
                .replace("§7", reset.fg(Ansi.Color.WHITE).boldOff().toString())
                .replace("§8", reset.fg(Ansi.Color.BLACK).bold().toString())
                .replace("§9", reset.fg(Ansi.Color.BLUE).bold().toString())
                .replace("§a", reset.fg(Ansi.Color.GREEN).bold().toString())
                .replace("§b", reset.fg(Ansi.Color.CYAN).bold().toString())
                .replace("§c", reset.fg(Ansi.Color.RED).bold().toString())
                .replace("§d", reset.fg(Ansi.Color.MAGENTA).bold().toString())
                .replace("§e", reset.fg(Ansi.Color.YELLOW).bold().toString())
                .replace("§f", reset.fg(Ansi.Color.WHITE).bold().toString())
                .replace("§k", Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString())
                .replace("§l", Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString())
                .replace("§m", Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString())
                .replace("§n", Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString())
                .replace("§o", Ansi.ansi().a(Ansi.Attribute.ITALIC).toString())
                .replace("§r", reset.toString()) + reset
        );
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        newVersion = Main.getInstance().getDescription().getVersion();
        saveDefaultConfig();
        PREFIX = getConfig().getString("prefix");
        printlnColor(PREFIX + "Starting...");

        long startTime = System.currentTimeMillis();
        configManager = new ConfigManager(this);

        if (!checkLicense()) return;

        if (Bukkit.getPluginManager().getPlugin("Fastbuilder").isEnabled()) {
            initializePlugin();

            printlnColor(PREFIX + "Took " + (System.currentTimeMillis() - startTime) + "ms to start the plugin!");
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
                    } catch (Exception ignored) {
                    }
                }
            }
            p.kickPlayer("Rejoin, please! Fastbuilder doesn't support reloads with players online.");
        });
    }

    public String getModeOfPlayer(Player player) {
        return this.mapWherePlayerIn.get(player).split("-")[0];
    }

    private boolean checkLicense() {
        try (Socket socket = new Socket("minusone-services.ddns.net", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(getConfig().getString("user"));
            out.println(getConfig().getString("license"));
            if (!Boolean.parseBoolean(in.readLine())) {
                printlnColor(PREFIX + "§cLICENSE NOT VALID. PLEASE BUY A LICENSE ON https://builtbybit.com/resources/fastbuilder.62493/ OR IF YOU BOUGHT THE PLUGIN REQUEST THE LICENSE ON https://discord.gg/CnQGtfEdjj");
                Bukkit.getPluginManager().disablePlugin(this);
                return false;
            }
            out.println("version");
            String latestVersion = in.readLine();
            if (!newVersion.equalsIgnoreCase(latestVersion)) {
                newVersion = latestVersion;
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    printlnColor(PREFIX + "§b" + newVersion + " IS NOW AVAILABLE!");
                    printlnColor(PREFIX + "DOWNLOAD IT ON https://builtbybit.com/resources/fastbuilder.62493/");
                }, 400L);
            }

        } catch (IOException e) {
            printlnColor(PREFIX + "§cError occurred: " + e.getClass().getSimpleName());
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }

    private void initializePlugin() {
        try {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                this.luckpermsApi = provider.getProvider();
                this.luckpermsSupport = true;
            }
        } catch (NoClassDefFoundError e) {
            if (configManager.isLuckPermsSupportEnabled())
                printlnColor(PREFIX + "§cDidn't find LuckPerms.");
        }

        this.timer = new Timer();
        this.scoreboardManager = new ScoreboardManager(this);
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
        blocks.put(Material.CLAY, 400);
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
        blocks.put(Material.BARRIER, 5000);
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new onPlayerJoinQuit(), this);
        Bukkit.getPluginManager().registerEvents(new onBlockPlaceBreak(), this);
        Bukkit.getPluginManager().registerEvents(new onPlayerMove(), this);
        Bukkit.getPluginManager().registerEvents(new onPlayerInteract(), this);
        Bukkit.getPluginManager().registerEvents(new onInventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new onEntityDamage(), this);
        Bukkit.getPluginManager().registerEvents(new onPlayerSneak(), this);
        Bukkit.getPluginManager().registerEvents(new onItemDrop(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        protocolManager.addPacketListener(new onPlayerBlockPlacePacket());
        protocolManager.addPacketListener(new onPlayerClickEntity());
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
                }
            }
        }.runTaskTimer(this, 0L, 1L);

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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0L, 1L);
    }

    public ConfigManager getConfigManager() {
        return configManager;
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
