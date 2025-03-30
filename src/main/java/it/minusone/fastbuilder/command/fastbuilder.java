package it.minusone.fastbuilder.command;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.config.ConfigManager;
import it.minusone.fastbuilder.items.IslandSelector;
import it.minusone.fastbuilder.items.ReplayItem;
import it.minusone.fastbuilder.items.SettingsItem;
import it.minusone.fastbuilder.npc.NPC;
import it.minusone.fastbuilder.npc.NPCManager;
import it.minusone.fastbuilder.utils.bukkit.entity.HologramStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;

public class fastbuilder implements CommandExecutor {
    private final ConfigManager configManager;

    public fastbuilder() {
        this.configManager = new ConfigManager(Main.getInstance());
    }

    public static void teleportPlayerToIsland(Player player, String mode, String island) {
        if (Main.getInstance().modesWherePlayerIn.containsValue(mode) && Main.getInstance().islandWherePlayerIn.containsValue(island)) {
            player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Island is already occupied by " + Objects.requireNonNull(Main.getInstance().islandWherePlayerIn.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().equals(island))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null)).getName() + "!");
            return;
        }
        Location location = Main.getInstance().getConfigManager().getSpawnLocation(mode, island);
        player.teleport(location);
        player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Joined " + mode + "-" + island + "!");
        ItemStack block = Main.getInstance().getConfigManager().getCurrentPlayerBlock(player);
        block.setAmount(64);
        player.getInventory().setItem(0, block);
        player.getInventory().setItem(1, block);
        player.getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
        player.getInventory().setItem(4, new IslandSelector());
        player.getInventory().setItem(6, new ReplayItem());
        player.getInventory().setItem(7, new SettingsItem());
        Main.getInstance().modesWherePlayerIn.put(player, mode);
        Main.getInstance().islandWherePlayerIn.put(player, island);
        Location baseLocationA = location.clone();
        Location offsetA = Main.getInstance().getConfigManager().getNPCOffset(mode);
        baseLocationA.setWorld(player.getWorld());
        baseLocationA.add(offsetA.getX(), offsetA.getY(), offsetA.getZ());
        NPC modeSelector = new NPC(player, "Mode Changer", baseLocationA);
        if (NPCManager.getNPC(player) != null)
            NPCManager.removeNPC(NPCManager.getNPC(player), player);
        modeSelector.spawn(player);
        NPCManager.setNPC(modeSelector, player);
        Location baseLocationB = location.clone();
        Location offsetB = Main.getInstance().getConfigManager().getHoloOffset(mode, player.getLocation().getWorld());
        baseLocationB.setWorld(player.getWorld());
        baseLocationB.add(offsetB.getX(), offsetB.getY(), offsetB.getZ());
        HologramStack stats = new HologramStack(4, 0.3, true, false,
                "Successful Attempts: " + Main.getInstance().getConfigManager().getSuccessfulAttempts(player),
                "Total Attempts: " + Main.getInstance().getConfigManager().getAttempts(player),
                "Personal Best: " + (!new DecimalFormat("0.000").format(Main.getInstance().getConfigManager().getPB(player.getUniqueId(), mode)).replace(",", ".").equals("0.000") ? new DecimalFormat("0.000").format(Main.getInstance().getConfigManager().getPB(player.getUniqueId(), mode)).replace(",", ".") : "-.---"),
                ChatColor.AQUA + player.getName() + "§r's stats:"
        );
        if (Main.getInstance().stats.containsKey(player)) {
            Main.getInstance().stats.get(player).despawn();
            Main.getInstance().stats.get(player).delete();
            Main.getInstance().stats.remove(player);
        }
        stats.setLocation(baseLocationB);
        stats.spawn();
        Main.getInstance().stats.put(player, stats);
        Main.getInstance().scoreboardManager.updateTimer(player, "0.000", Main.getInstance().getConfig().getDouble("best-times." + player.getUniqueId() + "." + mode, 0.000));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getInstance().PREFIX + "§cYou must be a player to execute this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Main.getInstance().PREFIX + "This server is running Fastbuilder by MinusOne! Buy it on https://builtbybit.com/resources/fastbuilder.62493/.");
            if (player.hasPermission("fastbuilder.admin")) {
                player.sendMessage(Main.getInstance().PREFIX + "List of commands:");
                player.sendMessage(Main.getInstance().PREFIX + "/fb join <mode> <island_number> - To join a mode");
                player.sendMessage(Main.getInstance().PREFIX + "/fb addmode <mode> - To add a mode");
                player.sendMessage(Main.getInstance().PREFIX + "/fb removemode <mode> - To remove a mode");
                player.sendMessage(Main.getInstance().PREFIX + "/fb set <mode> <island_number> - To set spawn of an island of a mode");
                player.sendMessage(Main.getInstance().PREFIX + "/fb setcoins <player> <coins> - To set coins of a player.");
                player.sendMessage(Main.getInstance().PREFIX + "/fb addcoins <player> <coins> - To add coins to a player.");
                player.sendMessage(Main.getInstance().PREFIX + "/fb reloadconfig - To reload plugin config.");
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "addcoins":
            case "setcoins":
            case "addmode":
            case "removemode":
            case "set":
            case "reloadconfig":
                if (!player.hasPermission("fastbuilder.admin")) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
                break;
        }

        switch (args[0].toLowerCase()) {
            case "addcoins":
                if (args.length < 3) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Usage: /fastbuilder addcoins <player> <coins>");
                    return true;
                }
                Main.getInstance().getConfigManager().setCoinsOfPlayer(Bukkit.getPlayer(args[1]), Main.getInstance().getConfigManager().getCoinsOfPlayer(player) + Integer.parseInt(args[2]));
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Added " + args[2] + " coins to " + args[1] + ".");
                break;

            case "setcoins":
                if (args.length < 3) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Usage: /fastbuilder setcoins <player> <coins>");
                    return true;
                }
                Main.getInstance().getConfigManager().setCoinsOfPlayer(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Set " + args[2] + " coins to " + args[1] + ".");
                break;

            case "addmode":
                if (args.length < 2) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Usage: /fastbuilder addmode <mode>");
                    return true;
                }
                String modeToAdd = args[1];
                configManager.addMode(modeToAdd);
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Mode added: " + modeToAdd);
                break;

            case "removemode":
                if (args.length < 2) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Usage: /fastbuilder removemode <mode>");
                    return true;
                }
                String modeToRemove = args[1];
                if (!configManager.modeExists(modeToRemove)) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Mode does not exist!");
                    return true;
                }
                configManager.removeMode(modeToRemove);
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Mode removed: " + modeToRemove);
                break;

            case "set":
                if (args.length < 3) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Usage: /fastbuilder set <mode> <island>");
                    return true;
                }

                String mode = args[1];
                String island = args[2];

                if (!configManager.modeExists(mode)) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Mode does not exist! Use /fastbuilder addmode <mode> first.");
                    return true;
                }

                Location spawnLocation = player.getLocation();
                Main.getInstance().getConfigManager().setSpawnLocation(mode, island, spawnLocation);

                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Spawn set for " + mode + "-" + island);
                break;
            case "reloadconfig":
                player.sendMessage(Main.getInstance().PREFIX + "Reloading config...");
                Main.getInstance().getConfigManager().reloadConfig();
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Config reloaded!");
                break;
            case "join":
                if (args.length < 2) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Usage: /fastbuilder join <mode> <island_number>");
                    return true;
                }

                String joinMode = args[1];

                if (args.length < 3) {
                    if (!configManager.modeExists(joinMode)) {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Mode does not exist!");
                        return true;
                    }

                    String closestIsland = findClosestIsland(joinMode);
                    if (closestIsland == null) {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "No islands found in this mode!");
                        return true;
                    }

                    Location joinLocation = configManager.getSpawnLocation(joinMode, closestIsland);
                    if (joinLocation == null) {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Island does not exist!");
                        return true;
                    }

                    Main.getInstance().removePlayerFromLeaderboard(player, Main.getInstance().modesWherePlayerIn.get(player));

                    leaveMode(player);


                    teleportPlayerToIsland(player, joinMode, closestIsland);
                } else {
                    String joinIsland = args[2];

                    if (!configManager.modeExists(joinMode)) {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Mode does not exist!");
                        return true;
                    }

                    Location joinLocation = configManager.getSpawnLocation(joinMode, joinIsland);
                    if (joinLocation == null) {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Island does not exist!");
                        return true;
                    }

                    Main.getInstance().removePlayerFromLeaderboard(player, Main.getInstance().modesWherePlayerIn.get(player));
                    leaveMode(player);
                    teleportPlayerToIsland(player, joinMode, joinIsland);
                }
                break;

            default:
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Unknown command! Use /fastbuilder to see list of commands.");
                break;
        }
        return true;
    }

    private String findClosestIsland(String mode) {
        for (String island : Main.getInstance().getConfig().getConfigurationSection("spawns." + mode).getKeys(false)) {
            if (Main.getInstance().islandWherePlayerIn.containsValue(island) && Main.getInstance().modesWherePlayerIn.containsValue(mode)) {
                continue; // Skip islands that are already occupied
            }

            return island; // Return the first available island in the list
        }

        return null; // No available island found
    }

    private void leaveMode(Player player) {
        String currentMode = Main.getInstance().modesWherePlayerIn.get(player);
        String currentIsland = Main.getInstance().islandWherePlayerIn.get(player);

        if (currentMode != null && currentIsland != null) {

            Main.getInstance().modesWherePlayerIn.remove(player);
            Main.getInstance().islandWherePlayerIn.remove(player);
            Main.getInstance().removePlayerFromLeaderboard(player, Main.getInstance().modesWherePlayerIn.get(player));
            if (NPCManager.getNPC(player) != null) {
                NPCManager.removeNPC(NPCManager.getNPC(player), player);
            }

            player.getInventory().clear();
            Location spawn = Main.getInstance().getConfigManager().getSpawnLocation(currentMode, currentIsland);
            if (spawn != null) {
                player.teleport(spawn);
            }
            Main.getInstance().stats.get(player).despawn();
            Main.getInstance().stats.get(player).delete();
            Main.getInstance().stats.remove(player);
            player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "You have left " + currentMode + "-" + currentIsland + "!");
        }
    }


}
