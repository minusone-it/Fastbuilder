package it.minusone.fastbuilder;

import it.minusone.fastbuilder.command.fastbuilder;
import it.minusone.fastbuilder.command.tabcompleter.fastbuilderTabCompleter;
import it.minusone.fastbuilder.config.ConfigManager;
import it.minusone.fastbuilder.listeners.*;
import it.minusone.fastbuilder.scoreboard.ScoreboardManager;
import it.minusone.fastbuilder.timer.Timer;
import it.minusone.fastbuilder.utils.PressurePlateFinder;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public final class Main extends JavaPlugin {

    public String PREFIX;
    public ScoreboardManager scoreboardManager;
    public HashMap<Player, String> modesWherePlayerIn;
    public HashMap<Player, String> islandWherePlayerIn;
    public HashMap<String, List<HashMap<Double, Player>>> tops;
    public HashMap<UUID, List<Location>> placedBlocks;
    public Timer timer;
    public ConfigManager configManager;
    public static List<Location> plateAboveLocations;
    private static Main INSTANCE;

    @Override
    public void onEnable() {
        this.PREFIX = getConfig().getString("prefix");
        this.getLogger().info(this.PREFIX + "Starting...");
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
                this.getLogger().severe("LICENSE NOT VALID. PLEASE BUY A LICENSE ON https://builtbybit.com/resources/fastbuilder.62493/ OR IF YOU BOUGHT PLUGIN GET A LICENSE ON https://discord.gg/CnQGtfEdjj.");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        } catch (IOException e) {
            this.getLogger().severe("Error while trying to connect to the license server. Please contact support on discord.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        INSTANCE = this;
        if (Bukkit.getPluginManager().getPlugin("Fastbuilder").isEnabled()) {
            this.timer = new Timer();
            this.scoreboardManager = new ScoreboardManager();
            this.modesWherePlayerIn = new HashMap<>();
            this.islandWherePlayerIn = new HashMap<>();
            this.placedBlocks = new HashMap<>();
            this.tops = new HashMap<>();
            this.loadListeners();
            this.loadCommands();
            for (Player p : Bukkit.getOnlinePlayers())
                p.kickPlayer("Rejoin, please! Fastbuilder doesn't support reloads with players online.");
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
            /**for (World world : Bukkit.getServer().getWorlds())
             * TODO
             *
                plateAboveLocations = PressurePlateFinder.getAirAbovePlates(this, world.getName());
            */
            this.getLogger().info(this.PREFIX + "Took " + (System.currentTimeMillis() - startTime) + "ms to start the plugin!");
        }
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            scoreboardManager.removeScoreboard(p);
            PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
            for (Entity entity : Bukkit.getServer().getWorlds().get(1).getEntities()) {
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, (EntityPlayer) entity));
                connection.sendPacket(new PacketPlayOutNamedEntitySpawn((EntityHuman) entity));
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
    }

    private void loadCommands() {
        this.getCommand("fastbuilder").setExecutor(new fastbuilder());
        this.getCommand("fastbuilder").setTabCompleter(new fastbuilderTabCompleter());
    }

    public static Main getInstance() {
        return INSTANCE;
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
        List<HashMap<Double, Player>> topList = tops.getOrDefault(mode, new ArrayList<>());

        HashMap<Double, Player> previousEntry = null;
        double previousTime = Double.MAX_VALUE;

        for (HashMap<Double, Player> entry : topList) {
            for (Map.Entry<Double, Player> mapEntry : entry.entrySet()) {
                if (mapEntry.getValue().equals(player)) {
                    previousEntry = entry;
                    previousTime = mapEntry.getKey();
                    break;
                }
            }
            if (previousEntry != null) break;
        }

        if (previousEntry != null && time >= previousTime) return;

        if (previousEntry != null) {
            topList.remove(previousEntry);
        }

        HashMap<Double, Player> newEntry = new HashMap<>();
        newEntry.put(time, player);
        topList.add(newEntry);

        topList.sort(Comparator.comparingDouble(map -> map.keySet().iterator().next()));

        while (topList.size() > 10) {
            topList.remove(topList.size() - 1);
        }

        tops.put(mode, topList);
    }

    public void removePlayerFromLeaderboard(Player player, String mode) {
        List<HashMap<Double, Player>> topList = tops.get(mode);

        if (topList == null) return;

        Iterator<HashMap<Double, Player>> iterator = topList.iterator();
        while (iterator.hasNext()) {
            HashMap<Double, Player> entry = iterator.next();
            for (Map.Entry<Double, Player> mapEntry : entry.entrySet()) {
                if (mapEntry.getValue().equals(player)) {
                    iterator.remove();
                    break;
                }
            }
        }
        tops.put(mode, topList);
    }

}
