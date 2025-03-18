package it.minusone.fastbuilder.command;

import it.minusone.fastbuilder.Main;
import it.minusone.fastbuilder.config.ConfigManager;
import it.minusone.fastbuilder.items.IslandSelector;
import it.minusone.fastbuilder.items.SettingsItem;
import it.minusone.fastbuilder.npc.NPC;
import it.minusone.fastbuilder.npc.NPCManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

public class fastbuilder implements CommandExecutor {
    private final ConfigManager configManager;

    public fastbuilder() {
        this.configManager = new ConfigManager(Main.getInstance());
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
                player.sendMessage(Main.getInstance().PREFIX + "/fb setcoins <player> <coins> - To set spawn of an island of a mode");
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "addmode":
            case "removemode":
            case "set":
                if (!player.hasPermission("fastbuilder.admin")) {
                    player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
                break;
        }

        switch (args[0].toLowerCase()) {
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
                Main.getInstance().getConfig().set("spawns." + mode + "." + island, spawnLocation.serialize());
                Main.getInstance().saveConfig();

                player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Spawn set for " + mode + " - " + island);
                Main.getInstance().configManager = null;
                Main.getInstance().configManager = new ConfigManager(Main.getInstance());
                break;
            case "join":
                String joinMode = args[1];
                if (args.length < 3) {
                    // Se non viene fornito il numero dell'isola, seleziona quella più vicina a (1, 0)
                    if (!configManager.modeExists(joinMode)) {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Mode does not exist!");
                        return true;
                    }

                    // Trova la posizione dell'isola più vicina a (1, 0)
                    String closestIsland = findClosestIsland(joinMode, 1, 0);
                    if (closestIsland == null) {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "No islands found in this mode!");
                        return true;
                    }

                    // Procedi con il join sull'isola trovata
                    Location joinLocation = configManager.getSpawnLocation(joinMode, closestIsland);
                    if (joinLocation == null) {
                        player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Island does not exist!");
                        return true;
                    }

                    teleportPlayerToIsland(player, joinMode, closestIsland, joinLocation);
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

                    teleportPlayerToIsland(player, joinMode, joinIsland, joinLocation);
                }
                break;


            default:
                player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Unknown command! Use /fastbuilder <addmode|removemode|set|join>");
                break;
        }
        return true;
    }
    private String findClosestIsland(String mode, double targetX, double targetZ) {
        String closestIsland = null;
        double closestDistance = Double.MAX_VALUE;

        // Itera attraverso tutte le isole di questo "mode"
        for (String island : Main.getInstance().getConfig().getConfigurationSection("spawns." + mode).getKeys(false)) {
            // Controlla se l'isola è già occupata
            if (Main.getInstance().islandWherePlayerIn.containsValue(island)) {
                continue; // Se è occupata, salta questa isola
            }

            Location islandLocation = Location.deserialize(Main.getInstance().getConfig().getConfigurationSection("spawns." + mode + "." + island).getValues(false));

            // Calcola la distanza tra la posizione dell'isola e (1, 0)
            double distance = islandLocation.distance(new Location(islandLocation.getWorld(), targetX, 0, targetZ));
            if (distance < closestDistance) {
                closestDistance = distance;
                closestIsland = island;
            }
        }

        return closestIsland;
    }


    private void teleportPlayerToIsland(Player player, String mode, String island, Location location) {
        if (Main.getInstance().modesWherePlayerIn.containsValue(mode) && Main.getInstance().islandWherePlayerIn.containsValue(island)) {
            player.sendMessage(Main.getInstance().PREFIX + ChatColor.RED + "Island is already occupied by " + Objects.requireNonNull(Main.getInstance().islandWherePlayerIn.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().equals(island))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null)).getName() + "!");
            return;
        }

        player.teleport(location);
        player.sendMessage(Main.getInstance().PREFIX + ChatColor.GREEN + "Joined " + mode + "-" + island + "!");
        player.getInventory().setItem(0, new ItemStack(Material.SANDSTONE, 64));
        player.getInventory().setItem(1, new ItemStack(Material.SANDSTONE, 64));
        player.getInventory().setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
        player.getInventory().setItem(4, new IslandSelector());
        player.getInventory().setItem(7, new SettingsItem());
        Main.getInstance().modesWherePlayerIn.put(player, mode);
        Main.getInstance().islandWherePlayerIn.put(player, island);
        location.setX(location.getX() - 1);
        NPC modeSelector = new NPC(player, "Mode Changer", location);
        if (NPCManager.getNPC(player) != null) {
            NPCManager.removeNPC(NPCManager.getNPC(player), player);
        }
        modeSelector.spawn(player);
        NPCManager.setNPC(modeSelector, player);
        Main.getInstance().scoreboardManager.updateTimer(player, "0.000", Main.getInstance().getConfig().getDouble("best-times." + player.getUniqueId() + "." + mode, 0.000));
    }

}
